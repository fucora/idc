package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.JOB_RUNTIME;
import static com.iwellmass.idc.quartz.IDCContextKey.TASK_JSON;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.BarrierState;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobEnv;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

public class IDCJobStoreTX extends JobStoreTX implements IDCJobStore {
	
	private final IDCDriverDelegate idcDriverDelegate;
	private final DependencyService dependencyService;
	
	IDCJobStoreTX(IDCDriverDelegate idcDelegate, DependencyService workflowService) {
		this.idcDriverDelegate = idcDelegate;
		this.dependencyService = workflowService;
	}
	
	/* 生成 JobInstanceId */
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
                    Task task = JSON.parseObject(TASK_JSON.applyGet(job.getJobDataMap()), Task.class);
                    JobEnv jobEnv = initJobEnv(task, nextTrigger);
					JobInstance ins = createJobInstance(task, jobEnv);
					// 计算依赖
					List<JobBarrier> barriers = task.getTaskType() == TaskType.SUB_TASK ? computeSubBarriers(conn, ins, jobEnv.getWorkflowId()) : computeBarriers(conn, ins);
					// clear first
					idcDriverDelegate.clearJobBarrier(conn, ins.getJobKey());
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
                    
        			////////////////////////////////////////
        			// save JobInstance
                    Integer fid = idcDriverDelegate.insertJobInstance(conn, ins).getInstanceId();
    				//////////////////////////////////////// END save
                    
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
	
	private JobEnv initJobEnv(Task task, OperableTrigger trigger) {
		JobEnv env = Optional.ofNullable(JOB_RUNTIME.applyGet(trigger.getJobDataMap()))
			.map(str -> JSON.parseObject(str, JobEnv.class))
			.orElseGet(JobEnv::new);
		env.setJobKey(new JobKey(trigger.getKey().getName(), trigger.getKey().getGroup()));
		env.setTaskKey(new TaskKey(trigger.getJobKey().getName(), trigger.getJobKey().getGroup()));

		// 构建默认值
		if (env.getShouldFireTime() == null) {
			env.setShouldFireTime(Optional.ofNullable(trigger.getNextFireTime()).map(Date::getTime).orElse(-1L));
		}
		if (env.getPrevFireTime() == null) {
			env.setPrevFireTime(Optional.ofNullable(trigger.getPreviousFireTime()).map(Date::getTime).orElse(-1L));
		}
		if (env.getTaskType() == null) {
			env.setTaskType(task.getTaskType());
		}
		return env;
	}
	
	
	private JobInstance createJobInstance(Task task, JobEnv jobEnv) {
		JobInstance jobInstance = new JobInstance();
		
		// ~~ 基本信息 ~~
		jobInstance.setTaskKey(task.getTaskKey());
		jobInstance.setContentType(task.getContentType());
		jobInstance.setDispatchType(task.getDispatchType());
		
		// ~~ 调度信息 ~~
		jobInstance.setTaskType(jobEnv.getTaskType());
		if (jobEnv.getTaskType() == TaskType.SUB_TASK) {
			jobInstance.setMainInstanceId(jobEnv.getMainInstanceId());
		}
		
		// id
		jobInstance.setInstanceId(jobEnv.getInstanceId());
		// 所属计划
		jobInstance.setJobKey(jobEnv.getJobKey());
		// 责任人
		jobInstance.setAssignee(jobEnv.getAssignee());
		// 调度类型
		jobInstance.setScheduleType(jobEnv.getScheduleType());
		// 批次
		jobInstance.setShouldFireTime(jobEnv.getShouldFireTime());
		// prev 批次
		jobInstance.setPrevFireTime(jobEnv.getPrevFireTime());
		// 业务日期
		ScheduleType st = jobInstance.getScheduleType();
		// 批次
		jobInstance.setLoadDate(st.format(IDCUtils.toLocalDateTime(jobEnv.getShouldFireTime())));
		// 其他
		jobInstance.setStartTime(LocalDateTime.now());
		jobInstance.setEndTime(null);
		jobInstance.setStatus(JobInstanceStatus.NEW);
		// parameter
		jobInstance.setParameter(jobEnv.getParameter());
		return jobInstance;
	}
	
	private List<JobBarrier> computeSubBarriers(Connection conn, JobInstance jr, String workflowId) throws SQLException {
		List<JobBarrier> barriers = new ArrayList<>();
		// 流程子任务，检查上游任务是否都已完成
		List<TaskKey> depTasks = dependencyService.getPredecessors(workflowId, jr.getTaskKey());
		if (!Utils.isNullOrEmpty(depTasks)) {
			for (TaskKey tk : depTasks) {
				JobKey barrierKey = IDCUtils.getSubJobKey(jr.getJobKey(), tk);
				JobBarrier b = buildBarrier(conn, jr.getJobKey(), barrierKey, jr.getShouldFireTime());
				if (b != null) {
					barriers.add(b);
				}
			}
		}
		return barriers;
	}
	private List<JobBarrier> computeBarriers(Connection conn, JobInstance jr) throws SQLException {
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
			if (ins == null || ins.getStatus() != JobInstanceStatus.FINISHED) {
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
			JobInstance ins = idcDriverDelegate.selectJobInstance(conn, event.getInstanceId());
			if (ins == null) {
				getLog().warn("任务 {} 不存在", event.getInstanceId());
				return null;
			}
			// 删除 barrier
			JobKey jobKey = ins.getJobKey();
			if (event.getFinalStatus() == JobInstanceStatus.FINISHED) {
				idcDriverDelegate.disableBarriers(conn, jobKey.getJobId(), jobKey.getJobGroup(), ins.getShouldFireTime());
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
	
}