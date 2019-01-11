package com.iwellmass.idc.quartz;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.quartz.JobDetail;
import org.quartz.JobPersistenceException;
import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.spi.OperableTrigger;

import com.alibaba.fastjson.JSON;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.common.param.ParamParser;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.BarrierState;
import com.iwellmass.idc.model.GuardEnv;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.RedoEnv;
import com.iwellmass.idc.model.SubEnv;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.model.WorkflowEdge;

public class IDCJobStoreTX extends JobStoreTX implements IDCJobStore {
	
	private final IDCDriverDelegate idcDriverDelegate;
	private final DependencyService dependencyService;
	
	IDCJobStoreTX(IDCDriverDelegate idcDelegate, DependencyService workflowService) {
		this.idcDriverDelegate = idcDelegate;
		this.dependencyService = workflowService;
	}
	
	protected String getFiredTriggerRecordId() {
		throw new UnsupportedOperationException("Please using JobInstance.getInstanceId().");
	}
	
	/* WAITING --> ACQUIRED */
	protected List<OperableTrigger> acquireNextTrigger(Connection conn, long noLaterThan, int maxCount, long timeWindow)
			throws JobPersistenceException {
        if (timeWindow < 0) {
          throw new IllegalArgumentException();
        }
        
        List<OperableTrigger> acquiredTriggers = new ArrayList<OperableTrigger>();
        Set<org.quartz.JobKey> acquiredJobKeysForNoConcurrentExec = new HashSet<org.quartz.JobKey>();
        final int MAX_DO_LOOP_RETRY = 3;
        int currentLoopCount = 0;
        do {
            currentLoopCount ++;
            
            // 并发控制
            List<JobInstance> runningJobs = idcDriverDelegate.selectRuningJobs();
            if (runningJobs.size() > 5) { // hard-value
            	continue;
            }
            
            try {
                List<TriggerKey> keys = getDelegate().selectTriggerToAcquire(conn, noLaterThan + timeWindow, getMisfireTime(), maxCount);
                
                // No trigger is ready to fire yet.
                if (keys == null || keys.size() == 0)
                    return acquiredTriggers;

                long batchEnd = noLaterThan;

                for(TriggerKey triggerKey: keys) {
                    // If our trigger is no longer available, try a new one.
                    OperableTrigger nextTrigger = retrieveTrigger(conn, triggerKey);
                    if(nextTrigger == null) {
                        continue; // next trigger
                    }
                    
                    // If trigger's job is set as @DisallowConcurrentExecution, and it has already been added to result, then
                    // put it back into the timeTriggers set and continue to search for next trigger.
                    org.quartz.JobKey jobKey = nextTrigger.getJobKey();
                    JobDetail job;
                    try {
                        job = retrieveJob(conn, jobKey);
                    } catch (JobPersistenceException jpe) {
                        try {
                            getLog().error("Error retrieving job, setting trigger state to ERROR.", jpe);
                            getDelegate().updateTriggerState(conn, triggerKey, STATE_ERROR);
                        } catch (SQLException sqle) {
                            getLog().error("Unable to set trigger state to ERROR.", sqle);
                        }
                        continue;
                    }
                    
                    if (job.isConcurrentExectionDisallowed()) {
                        if (acquiredJobKeysForNoConcurrentExec.contains(jobKey)) {
                            continue; // next trigger
                        } else {
                            acquiredJobKeysForNoConcurrentExec.add(jobKey);
                        }
                    }
                    
                    if (nextTrigger.getNextFireTime().getTime() > batchEnd) {
                      break;
                    }
                    
                    /////////////////////////////////////// BEGIN check
					// check barrier
					////////////////
                    IDCTriggerInstruction ti = IDCContextKey.JOB_TRIGGER_INSTRUCTION.applyGet(nextTrigger.getJobDataMap());
                    JobKey idcJobKey = null;
                    JobInstance ins = null;
                    List<JobBarrier> barriers = Collections.emptyList();
                    String fid = null;
                    if (ti == IDCTriggerInstruction.MAIN) {
                    	
                    	TaskKey idcTaskKey = new TaskKey(job.getKey().getName(), job.getKey().getGroup());
                    	idcJobKey = new JobKey(nextTrigger.getKey().getName(), nextTrigger.getKey().getGroup());

                    	Task idcTask = idcDriverDelegate.selectTask(idcTaskKey);
                    	Job idcJob = idcDriverDelegate.selectJob(idcJobKey);
                    	
                    	ins = new JobInstance();
                		// ~~ 基础信息 ~~
                		ins.setTaskKey(idcTask.getTaskKey());
                		ins.setContentType(idcTask.getContentType());
                		
                		// ~~ 调度信息 ~~
                		ins.setJobKey(idcJob.getJobKey());
                		ins.setJobName(idcJob.getJobName());
                		ins.setDispatchType(idcJob.getDispatchType());
                		ins.setTaskType(idcJob.getTaskType());
                		ins.setAssignee(idcJob.getAssignee());
                		ins.setScheduleType(idcJob.getScheduleType());
                		ins.setWorkflowId(idcJob.getWorkflowId());
                		
                		// ~~ 运行时信息 ~~
                		// 批次
                		ins.setShouldFireTime(Optional.ofNullable(nextTrigger.getNextFireTime()).map(Date::getTime).orElse(-1L));
                		// prev 批次
                		ins.setPrevFireTime(Optional.ofNullable(nextTrigger.getPreviousFireTime()).map(Date::getTime).orElse(-1L));
                		// 业务日期
                		ins.setLoadDate(ins.getScheduleType().format(IDCUtils.toLocalDateTime(ins.getShouldFireTime())));
                		
                		// ~~ 其他 ~~
                		ins.setStartTime(LocalDateTime.now());
                		ins.setEndTime(null);
                		ins.setStatus(JobInstanceStatus.NEW);
                		
                		
                		// 计算参数
                		List<ExecParam> params = idcJob.getParameter();
                		if (!Utils.isNullOrEmpty(params)) {
                			IDCDefaultParam dp = new IDCDefaultParam();
                			dp.setShouldFireTime(IDCUtils.toLocalDateTime(ins.getShouldFireTime()));
                			ParamParser parser = new ParamParser(Collections.singletonMap("idc", dp));
                			parser.parse(params);
                			ins.setParameter(params);
                		}
                    	
                    	barriers = computeJobBarriers(conn, ins);
                    } else if (ti == IDCTriggerInstruction.SUB) {
                    	
                    	SubEnv jobEnv = JSON.parseObject(IDCContextKey.JOB_RUNTIME.applyGet(nextTrigger.getJobDataMap()), SubEnv.class);
                    	
                    	TaskKey idcTaskKey = new TaskKey(job.getKey().getName(), job.getKey().getGroup());
                    	idcJobKey = new JobKey(nextTrigger.getKey().getName(), nextTrigger.getKey().getGroup());
                    	
                    	Task idcTask = idcDriverDelegate.selectTask(idcTaskKey);
                    	JobInstance mainJobIns = idcDriverDelegate.selectJobInstance(conn, jobEnv.getMainInstanceId());
                    	Job mainIdcJob = idcDriverDelegate.selectJob(mainJobIns.getJobKey());
                    	
                    	ins = new JobInstance();
                		// ~~ 基础信息 ~~
                    	ins.setJobKey(idcJobKey);
                    	ins.setJobName(idcTask.getTaskName());
                		ins.setTaskKey(idcTask.getTaskKey());
                		ins.setContentType(idcTask.getContentType());
                		ins.setTaskType(TaskType.SUB_TASK);
                		// ~~ seam as main job ~~
                		ins.setDispatchType(mainJobIns.getDispatchType());
                		// 责任人
                		ins.setAssignee(mainJobIns.getAssignee());
                		// 调度类型
                		ins.setScheduleType(mainJobIns.getScheduleType());
                		// parameter
                		ins.setParameter(mainJobIns.getParameter());
                		// 批次
                		ins.setShouldFireTime(mainJobIns.getShouldFireTime());
                		// prev 批次
                		ins.setPrevFireTime(mainJobIns.getPrevFireTime());
                		// 业务日期
                		ins.setLoadDate(mainJobIns.getLoadDate());
                		
                		// ~~ 其他 ~~
                		ins.setMainInstanceId(jobEnv.getMainInstanceId());
                		ins.setStartTime(LocalDateTime.now());
                		ins.setEndTime(null);
                		ins.setStatus(JobInstanceStatus.NEW);
                		
                		barriers = computeWorkflowBarriers(conn, mainIdcJob, ins);
                    } else if (ti == IDCTriggerInstruction.REDO){
                    	RedoEnv redoEnv = JSON.parseObject(IDCContextKey.JOB_RUNTIME.applyGet(nextTrigger.getJobDataMap()), RedoEnv.class);
                    	ins = idcDriverDelegate.updateJobInstance(conn, redoEnv.getInstanceId(), (i)->{
                    		i.setStatus(JobInstanceStatus.NONE);
                    		i.setStartTime(LocalDateTime.now());
                    		i.setEndTime(null);
                    	});
                    	idcJobKey = ins.getJobKey();
                    	fid = redoEnv.getInstanceId().toString();
                    } else if (ti == IDCTriggerInstruction.GUARD) {
                    	GuardEnv guardEnv = JSON.parseObject(IDCContextKey.JOB_RUNTIME.applyGet(nextTrigger.getJobDataMap()), GuardEnv.class);
                    	idcJobKey = new JobKey(nextTrigger.getKey().getName(), nextTrigger.getKey().getGroup());
                    	barriers = new LinkedList<>();
                    	for (JobKey bk : guardEnv.getBarrierKeys()) {
                    		JobBarrier barrier = buildBarrier(conn, idcJobKey, bk, guardEnv.getShouldFireTime());
                    		if (barrier != null) {
                    			barriers.add(barrier);
                    		}
                    	}
                    	fid = System.currentTimeMillis() + "";
                    } else {
                    	throw new UnsupportedOperationException("not supported yet.");
                    }
                    // clear first
                    idcDriverDelegate.clearJobBarrier(conn, idcJobKey);
					// double check
					if (!barriers.isEmpty()) {
						idcDriverDelegate.batchInsertJobBarrier(conn, barriers);
						continue;
					}
					//////////////////////////////////////// END check
                    
                    
                    // We now have a acquired trigger, let's add to return list.
                    // If our trigger was no longer in the expected state, try a new one.
                    int rowsUpdated = getDelegate().updateTriggerStateFromOtherState(conn, triggerKey, STATE_ACQUIRED, STATE_WAITING);
                    if (rowsUpdated <= 0) {
                        continue; // next trigger
                    }
                    
                    if (fid == null) {
                    	fid = idcDriverDelegate.insertJobInstance(conn, ins).getInstanceId().toString();
                    }
                    
                    nextTrigger.setFireInstanceId(fid.toString());
                    
                    getDelegate().insertFiredTrigger(conn, nextTrigger, STATE_ACQUIRED, null);

                    if(acquiredTriggers.isEmpty()) {
                        batchEnd = Math.max(nextTrigger.getNextFireTime().getTime(), System.currentTimeMillis()) + timeWindow;
                    }
                    acquiredTriggers.add(nextTrigger);
                }

                // if we didn't end up with any trigger to fire from that first
                // batch, try again for another batch. We allow with a max retry count.
                if(acquiredTriggers.size() == 0 && currentLoopCount < MAX_DO_LOOP_RETRY) {
                    continue;
                }
                
                // We are done with the while loop.
                break;
            } catch (Exception e) {
                throw new JobPersistenceException(
                          "Couldn't acquire next trigger: " + e.getMessage(), e);
            }
        } while (true);
        
        // Return the acquired trigger list
        return acquiredTriggers;
	}
	
	private List<JobBarrier> computeWorkflowBarriers(Connection conn, Job mainJob, JobInstance ins) throws SQLException {
		List<JobBarrier> barriers = new ArrayList<>();
		// 流程子任务，检查上游任务是否都已完成
		List<TaskKey> depTasks = dependencyService.getPredecessors(mainJob.getTaskKey(), ins.getTaskKey());
		if (!Utils.isNullOrEmpty(depTasks)) {
			for (TaskKey deptk : depTasks) {
				if (deptk.equals(WorkflowEdge.START)) {
					continue;
				}
				JobKey barrierKey = IDCUtils.getSubJobKey(mainJob.getJobKey(), deptk);
				JobBarrier b = buildBarrier(conn, ins.getJobKey(), barrierKey, ins.getShouldFireTime());
				if (b != null) {
					barriers.add(b);
				}
			}
		}
		return barriers;
	}
	
	private List<JobBarrier> computeJobBarriers(Connection conn, JobInstance jr) throws SQLException {
		List<JobBarrier> barriers = new ArrayList<>();
		// 流程子任务，检查上游任务是否都已完成
		// 同周期任务是否完成
		JobBarrier b = buildBarrier(conn, jr.getJobKey(), jr.getJobKey(), jr.getPrevFireTime());
		if (b != null) {
			barriers.add(b);
		}
		// 任务间依赖
		List<JobDependency> jobDependencies = dependencyService.getJobDependencies(jr.getJobKey());
		if (!Utils.isNullOrEmpty(jobDependencies)) {
			for (JobDependency jdep : jobDependencies) {
				Long shouldFireTime = jr.getShouldFireTime();
				// TODO 计算 shouldFireTime
				JobBarrier a = buildBarrier(conn, jr.getJobKey(), jdep.getDependencyJobKey(), shouldFireTime);
				if (a != null) {
					barriers.add(a);
				}
			}
		}
		return barriers;
	}
	
	private JobBarrier buildBarrier(Connection conn, JobKey jobKey, JobKey barrierKey, Long barrierShouldFireTime) throws SQLException {
		if (barrierShouldFireTime != null && barrierShouldFireTime != -1) {
			JobInstance ins = idcDriverDelegate.selectJobInstance(conn, barrierKey, barrierShouldFireTime);
			
			Set<JobInstanceStatus> finished = new HashSet<>(Arrays.asList(JobInstanceStatus.FINISHED, JobInstanceStatus.SKIPPED));
			if (ins == null || !finished.contains(ins.getStatus())) {
				JobBarrier b = new JobBarrier();
				b.setJobKey(jobKey);
				b.setBarrierKey(barrierKey);
				b.setBarrierShouldFireTime(barrierShouldFireTime);
				b.setState(BarrierState.VALID);
				return b;
			}
		}
		return null;
	}

	@Override
	public JobInstance retrieveIDCJobInstance(Integer instanceId) {
		try {
			return executeWithoutLock(new TransactionCallback<JobInstance>() {
				@Override
				public JobInstance execute(Connection conn) throws JobPersistenceException {
					try {
						return idcDriverDelegate.selectJobInstance(conn, instanceId);
					} catch (SQLException e) {
						throw new JobPersistenceException(e.getMessage(), e);
					}
				}
			});
		} catch (JobPersistenceException e) {
			throw new AppException(e.getMessage(), e);
		}
	}
	
	@Override
	public void storeIDCJobInstance(Integer instanceId, Consumer<JobInstance> func)  {
		try {
			executeInLock(LOCK_STATE_ACCESS, new TransactionCallback<Void>() {
				@Override
				public Void execute(Connection conn) throws JobPersistenceException {
					try {
						idcDriverDelegate.updateJobInstance(conn, instanceId, func);
					} catch (SQLException e) {
						throw new JobPersistenceException(e.getMessage(), e);
					}
					return null;
				}
			});
		} catch (JobPersistenceException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	@Override
	public JobInstance completeIDCJobInstance(CompleteEvent event) throws JobPersistenceException {
		return (JobInstance) executeInLock(null, new TransactionCallback<JobInstance>() {
			@Override
			public JobInstance execute(Connection conn) throws JobPersistenceException {
				return completeIDCJobInstance(conn, event);
			}
		});
	}
	
	protected JobInstance completeIDCJobInstance(Connection conn, CompleteEvent event) throws JobPersistenceException {
		try {
			JobInstance ins = idcDriverDelegate.updateJobInstance(conn, event.getInstanceId(), (e -> {
				e.setStatus(event.getFinalStatus());
				e.setEndTime(event.getEndTime());
			}));
			if (ins == null) {
				return null;
			}
			
			JobKey jobKey = ins.getJobKey();
			// 删除 barrier
			if (buildBarrier(conn, jobKey, jobKey, ins.getShouldFireTime()) == null) {
				idcDriverDelegate.markBarrierInvalid(conn, jobKey.getJobId(), jobKey.getJobGroup(), ins.getShouldFireTime());
			}
			signalSchedulingChangeOnTxCompletion(0L);
			return ins;
		} catch (SQLException e) {
			throw new JobPersistenceException(e.getMessage(), e);
		}
	}
	
	@Override
	public void clearAllBarrier() {
		getLog().info("清空所有 Barrier 信息");
		try {
			executeInLock(null, new TransactionCallback<Void>() {

				@Override
				public Void execute(Connection conn) throws JobPersistenceException {
					try {
						idcDriverDelegate.clearAllBarrier(conn);
					} catch (SQLException e) {
						getLog().warn("清空 Barrier 信息失败: " + e.getMessage());
					}
					return null;
				}
			});
		} catch (JobPersistenceException e) {
			getLog().warn("清空 Barrier 信息失败: " + e.getMessage());
		}
	}
	
	@Override
	public List<JobInstance> retrieveIDCSubJobInstance(Integer mainInstanceId) throws JobPersistenceException {
		return executeWithoutLock(new TransactionCallback<List<JobInstance>>() {
			@Override
			public List<JobInstance> execute(Connection conn) throws JobPersistenceException {
				try {
					return idcDriverDelegate.selectSubJobInstance(conn, mainInstanceId);
				} catch (SQLException e) {
					throw new JobPersistenceException(e.getMessage(), e);
				}
			}
		});
	}

	@Override
	public void cleanupIDCJob(JobKey jobKey) throws JobPersistenceException {
		executeWithoutLock(new TransactionCallback<Void>() {
			@Override
			public Void execute(Connection conn) throws JobPersistenceException {
				try {
					idcDriverDelegate.cleanupJobInstance(conn, jobKey);
					idcDriverDelegate.clearJobBarrier(conn, jobKey);
				} catch (SQLException e) {
					throw new JobPersistenceException(e.getMessage(), e);
				}
				return null;
			}
		});
	}

}