package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.JOB_JSON;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_RUNTIME;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.ParameterParser;
import com.iwellmass.idc.TaskService;
import com.iwellmass.idc.WorkflowService;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.BarrierState;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.JobRuntime;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

public class IDCJobStoreTX extends JobStoreTX implements IDCJobStore {
	
	private final IDCDriverDelegate idcDriverDelegate;
	private final WorkflowService workflowService;
	private TaskService taskService;
	
	IDCJobStoreTX(IDCDriverDelegate idcDelegate, WorkflowService workflowService) {
		this.idcDriverDelegate = idcDelegate;
		this.workflowService = workflowService;
	}
	
	/* 生成 JobInstanceId */
	protected String getFiredTriggerRecordId() {
		return idcDriverDelegate.nextInstanceId().toString();
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
                    // We now have a acquired trigger, let's add to return list.
                    // If our trigger was no longer in the expected state, try a new one.
                    int rowsUpdated = getDelegate().updateTriggerStateFromOtherState(conn, triggerKey, STATE_ACQUIRED, STATE_WAITING);
                    if (rowsUpdated <= 0) {
                        continue; // next trigger
                    }
                    nextTrigger.setFireInstanceId(getFiredTriggerRecordId());
                    
					/////////////////////////////////////// BEGIN check
					// check barrier
					////////////////
					JobRuntime jr = initJobRuntime(nextTrigger);                    
					Task task = taskService.getTask(jr.getTaskKey());
					List<JobBarrier> barriers = computeBarriers(conn, task, jr);
					// clear first
					idcDriverDelegate.clearJobBarrier(conn, jr.getJobKey());
					// double check
					if (!barriers.isEmpty()) {
						idcDriverDelegate.batchInsertJobBarrier(conn, barriers);
						continue;
					}
					//////////////////////////////////////// END check
                    
                    getDelegate().insertFiredTrigger(conn, nextTrigger, STATE_ACQUIRED, null);

                    if(acquiredTriggers.isEmpty()) {
                        batchEnd = Math.max(nextTrigger.getNextFireTime().getTime(), System.currentTimeMillis()) + timeWindow;
                    }
                    acquiredTriggers.add(nextTrigger);
                    
        			////////////////////////////////////////
        			// save JobInstance & sequence barrier
        			//////////////////////////////////////
    				JobInstance ins = storeJobInstance(conn, jr, task);
    				// 主任务，添加 seq barrier
    				if (task.getTaskType() != TaskType.WORKFLOW_SUB_TASK) {
    					JobBarrier seqBarrier = buildSeqBarriers(ins);
    					idcDriverDelegate.batchInsertJobBarrier(conn, Collections.singletonList(seqBarrier));
    				} 
    				// 工作流任务，添加子任务barrier
    				else if (task.getTaskType() == TaskType.WORKFLOW_TASK) {
    					
    				}
    				//////////////////////////////////////// END save
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
	
	
	private JobInstance storeJobInstance(Connection conn, JobRuntime jr, Task task) throws JobPersistenceException, SQLException {
		
		JobInstance jobInstance = new JobInstance();
		// ~~ 基本信息 ~~
		jobInstance.setTaskId(task.getTaskId());
		jobInstance.setGroupId(task.getTaskGroup());
		jobInstance.setTaskName(task.getTaskName());
		jobInstance.setDescription(task.getDescription());
		jobInstance.setContentType(task.getContentType());
		jobInstance.setTaskType(task.getTaskType());
		jobInstance.setInstanceType(task.getDispatchType());
		jobInstance.setWorkflowId(task.getWorkflowId());
		
		// ~~ 运行时信息 ~~
		jobInstance.setJobId(jr.getJobKey().getJobId());
		jobInstance.setJobGroup(jr.getJobKey().getJobGroup());
		jobInstance.setAssignee(jr.getAssignee());
		jobInstance.setScheduleType(jr.getScheduleType());
		// instance id
		jobInstance.setInstanceId(jr.getInstanceId());
		// 执行参数
		jobInstance.setParameter(jr.getParameter());
		// 批次
		jobInstance.setShouldFireTime(jr.getShouldFireTime());
		// 业务日期
		ScheduleType st = jobInstance.getScheduleType();
		jobInstance.setLoadDate(st.format(IDCUtils.toLocalDateTime(jr.getShouldFireTime())));
		// 其他
		jobInstance.setStartTime(LocalDateTime.now());
		jobInstance.setEndTime(null);
		jobInstance.setStatus(JobInstanceStatus.NEW);
		// 父任务ID
		jobInstance.setWorkflowInstanceId(jr.getWorkflowInstanceId());
		return idcDriverDelegate.insertJobInstance(conn, jobInstance);
	}
	
	private JobRuntime initJobRuntime(OperableTrigger trigger) {
		Job job = JSON.parseObject(JOB_JSON.applyGet(trigger.getJobDataMap()), Job.class);
		JobRuntime jr = JSON.parseObject(JOB_RUNTIME.applyGet(trigger.getJobDataMap()), JobRuntime.class);
		if (jr == null) {
			jr = new JobRuntime();
		}
		// job key
		if (jr.getJobKey() == null) {
			jr.setJobKey(new JobKey(trigger.getKey().getName(), trigger.getKey().getGroup()));
		}
		if (jr.getTaskKey() == null) {
			jr.setTaskKey(IDCUtils.toTaskKey(trigger));
		}
		// id
		if (jr.getInstanceId() == null) {
			jr.setInstanceId(Integer.parseInt(trigger.getFireInstanceId()));
		}
		// should-fire-time
		if (jr.getShouldFireTime() == null) {
			jr.setShouldFireTime(trigger.getNextFireTime().getTime());
		}
		//
		if (jr.getPrevFireTime() == null) {
			Date p = trigger.getPreviousFireTime();
			jr.setPrevFireTime(p == null ? -1 : p.getTime());
		}
		// 责任人
		if (jr.getAssignee() == null) {
			jr.setAssignee(job.getAssignee());
		}
		// 调度类型
		if (jr.getScheduleType() == null) {
			jr.setScheduleType(job.getScheduleType());
		}
		// parameter
		ParameterParser parser = IDCContextKey.JOB_PARAMETER_PARSER.applyGet(trigger.getJobDataMap());
		jr.setParameter(parser.parse(job.getParameter(), jr.getParameter()));
		return jr;
	}
	
	private JobBarrier buildSeqBarriers(JobInstance ins) {
		JobBarrier barrier = new JobBarrier();
		barrier.setJobGroup(ins.getJobGroup());
		barrier.setJobId(ins.getJobId());
		barrier.setBarrierGroup(ins.getJobGroup());
		barrier.setBarrierId(ins.getJobId());
		barrier.setBarrierShouldFireTime(ins.getShouldFireTime());
		return barrier;
	}
	
	private List<JobBarrier> computeBarriers(Connection conn, Task task, JobRuntime jr) throws SQLException {
		List<JobBarrier> barriers = new ArrayList<>();
		// 流程子任务，检查上游任务是否都已完成
		if (task.getTaskType() == TaskType.WORKFLOW_SUB_TASK) {
			JobInstance wfIns = idcDriverDelegate.selectJobInstance(conn, jr.getWorkflowInstanceId());
			List<TaskKey> depTasks = workflowService.getPredecessors(wfIns.getWorkflowId(), task.getTaskKey());
			if (!Utils.isNullOrEmpty(depTasks)) {
				for (TaskKey tk : depTasks) {
					JobKey barrierKey = IDCUtils.getSubJobKey(jr.getJobKey(), tk);
					JobBarrier b = buildBarrier(conn, jr.getJobKey(), barrierKey, jr.getShouldFireTime());
					if (b != null) {
						barriers.add(b);
					}
				}
			}
		} else {
			// 同周期任务是否完成
			JobBarrier b = buildBarrier(conn, jr.getJobKey(), jr.getJobKey(), jr.getPrevFireTime());
			if (b != null) {
				barriers.add(b);
			}
			// 任务间依赖
			List<JobDependency> jobDependencies = idcDriverDelegate.selectJobDependencies(conn, jr.getJobKey());
			if (!Utils.isNullOrEmpty(jobDependencies)) {
				for (JobDependency jdep : jobDependencies) {
					// TODO 计算 shouldFireTime
					Long shouldFireTime = jr.getShouldFireTime();
					JobBarrier a = buildBarrier(conn, jr.getJobKey(), jdep.getDependencyJobKey(), shouldFireTime);
					if (a != null) {
						barriers.add(a);
					}
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
				idcDriverDelegate.deleteBarriers(conn, jobKey.getJobId(), jobKey.getJobGroup(), ins.getShouldFireTime());
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
						idcDriverDelegate.clearJobBarrier(conn);
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