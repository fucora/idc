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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.quartz.JobDetail;
import org.quartz.JobPersistenceException;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.OperableTrigger;

import com.alibaba.fastjson.JSON;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.common.param.ParamParser;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.model.BarrierState;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.model.WorkflowEdge;

public class IDCJobStoreTX extends JobStoreTX implements IDCJobStore {
	
	private final IDCDriverDelegate idcDriverDelegate;
	
	private final IDCPluginService pluginService;
	
	IDCJobStoreTX(IDCDriverDelegate idcDelegate, IDCPluginService pluginService) {
		this.idcDriverDelegate = idcDelegate;
		this.pluginService = pluginService;
	}
	
	@Override
	public void schedulerStarted() throws SchedulerException {
		super.schedulerStarted();
		IDCPluginConfig config = pluginService.getConfig();
		if (config.isBarrierClearOnStartup()) {
			try {
				idcDriverDelegate.clearAllBarrier(getConnection());
			} catch (SQLException e) {
				getLog().warn("clear barrier error: " + e.getMessage());
				// ignore 
			}
		}
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
        IDCPluginConfig config = pluginService.getConfig();
        do {
            currentLoopCount ++;
            
            // 并发控制 TODO 考虑这里是否合适
            List<JobInstance> runningJobs = idcDriverDelegate.selectRuningJobs();
            if (runningJobs.size() > config.getSchedulerParallelMax()) {
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
                    IDCTriggerHandler handler = newTriggerHandler(nextTrigger);
                    
                	List<JobBarrier> barriers = handler.computeBarriers(conn);
                	// clear first
                	idcDriverDelegate.clearJobBarrier(conn, handler.getIDCJobKey());
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

                    String fid = handler.storeJobInstance(conn);
                    
                    nextTrigger.setFireInstanceId(fid);
                    
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
	
	
	private boolean shouldBarrier(JobInstance barrierIns) {
		Set<JobInstanceStatus> finished = new HashSet<>(Arrays.asList(JobInstanceStatus.FINISHED, JobInstanceStatus.SKIPPED));
		return barrierIns == null || !finished.contains(barrierIns.getStatus());
	}
	/* ins 不再阻塞下游则返回 null，否则返回构建的 barrier 对象*/
	private JobBarrier buildBarrier(Connection conn, JobKey jobKey, JobInstance barrierIns) throws SQLException {
		JobBarrier b = new JobBarrier();
		b.setJobKey(jobKey);
		b.setBarrierKey(barrierIns.getJobKey());
		b.setBarrierShouldFireTime(barrierIns.getShouldFireTime());
		b.setState(BarrierState.VALID);
		return b;
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
	public JobInstance jobInstanceProgressing(ProgressEvent event) throws JobPersistenceException {
		return (JobInstance) executeInLock(LOCK_TRIGGER_ACCESS, new TransactionCallback<JobInstance>() {
			@Override
			public JobInstance execute(Connection conn) throws JobPersistenceException {
				try {
					JobInstance ins = idcDriverDelegate.selectJobInstance(conn, event.getInstanceId());
					if (ins != null) {
						// TODO should be CUSTOME Status
						ins.setStatus(event.getStatus());
						ins.setUpdateTime(event.getTime());
						if (ins.getTaskType() == TaskType.SUB_TASK) {
							JobInstance mainIns = idcDriverDelegate.selectJobInstance(conn, ins.getMainInstanceId());
							if (!mainIns.getStatus().isComplete()) {
								mainIns.setUpdateTime(ins.getUpdateTime());
							}
						}
					}
					return ins;
				} catch (SQLException e) {
					throw new JobPersistenceException(e.getMessage(), e);
				}
			}
		});
	}
	
	@Override
	public JobInstance jobInstanceCompleted(CompleteEvent event) throws JobPersistenceException {
		return (JobInstance) executeInLock(LOCK_TRIGGER_ACCESS, new TransactionCallback<JobInstance>() {
			@Override
			public JobInstance execute(Connection conn) throws JobPersistenceException {
				try {
					JobInstance ins = idcDriverDelegate.selectJobInstance(conn, event.getInstanceId());
					if (ins != null) {
						ins.setEndTime(event.getEndTime());
						ins.setStatus(event.getFinalStatus());
						idcDriverDelegate.updateJobInstance(conn, ins);
						
						// 删除 barrier
						if (!shouldBarrier(ins)) {
							idcDriverDelegate.markBarrierInvalid(conn, ins.getJobId(), ins.getJobGroup(), ins.getShouldFireTime());
							signalSchedulingChangeOnTxCompletion(0L);
						}
						

						if(ins.getTaskType() == TaskType.SUB_TASK) {
							JobInstance mainJobIns = idcDriverDelegate.selectJobInstance(conn, ins.getMainInstanceId());
							if (!mainJobIns.getStatus().isComplete()) {
								 Map<TaskKey, JobInstance> subTaskStatus = idcDriverDelegate.selectSubJobInstance(conn, mainJobIns.getInstanceId())
										.stream().collect(Collectors.toMap(JobInstance::getTaskKey, j -> j));
								List<WorkflowEdge> edges = pluginService.getTaskDependencies(mainJobIns.getTaskKey());
								for (WorkflowEdge edge : edges) {
									// TODO
									
								}
							}
						}
						
						
					}
					return ins;
				} catch (SQLException e) {
					throw new JobPersistenceException(e.getMessage(), e);
				}
			}
		});
	}
	
	@Override
	public void cleanupIDCJob(JobKey jobKey) throws JobPersistenceException {
		executeInLock(LOCK_TRIGGER_ACCESS, new TransactionCallback<Void>() {
			@Override
			public Void execute(Connection conn) throws JobPersistenceException {
				try {
					Job job = pluginService.getJob(jobKey);
					if (job == null) {
						return null;
					}
					// delete triggers
					removeTrigger(conn, new TriggerKey(jobKey.getJobId(), jobKey.getJobGroup()));
					if (job.getTaskType() == TaskType.WORKFLOW) {
						String group = IDCUtils.subJobGroup(jobKey);
						Set<TriggerKey> subTriggers = getDelegate().selectTriggersInGroup(conn, GroupMatcher.triggerGroupEquals(group));
						for (TriggerKey subTrigger : subTriggers) {
							removeTrigger(subTrigger);
						}
					}
					// delete all instance
					idcDriverDelegate.deleteJobInstance(conn, jobKey);
					// delete all barriers
					idcDriverDelegate.clearJobBarrier(conn, jobKey);
				} catch (SQLException e) {
					throw new JobPersistenceException(e.getMessage(), e);
				}
				return null;
			}
		});
	}
	
	private IDCTriggerHandler newTriggerHandler(OperableTrigger nextTrigger) {
		IDCTriggerInstruction instruction = IDCContextKey.JOB_TRIGGER_INSTRUCTION.applyGet(nextTrigger.getJobDataMap());
		switch (instruction) {
		case GUARD: {
			return new GuardHandler(nextTrigger);
		}
		case MAIN: {
			return new MainTaskHandler(nextTrigger);
		}
		case REDO: {
			return new RedoHandler(nextTrigger);
		}
		case SUB: {
			return new SubHandler(nextTrigger);
		}
		default:
			return new NotIDCTrigger(nextTrigger);
		}
	}

	private abstract class IDCTriggerHandler {

		OperableTrigger trigger;
		JobKey jobKey;
		TaskKey taskKey;
		
		IDCTriggerHandler(OperableTrigger trigger) {
			this.trigger = trigger;
			this.jobKey = new JobKey(trigger.getKey().getName(), trigger.getKey().getGroup());
			this.taskKey = new TaskKey(trigger.getJobKey().getName(), trigger.getJobKey().getGroup());
		}
		
		JobKey getIDCJobKey() {
			return jobKey;
		}
		
		TaskKey getIDCTaskKey() {
			return taskKey;
		}
		
		// 1. 验证有效性
		abstract void valid(Connection conn) throws SQLException;
		
		// 2. 计算 barriers
		abstract List<JobBarrier> computeBarriers(Connection conn) throws SQLException;
		
		// 3. 保存 JobInstance
		abstract String storeJobInstance(Connection conn) throws SQLException;

	}

	// 非 IDCJob
	private class NotIDCTrigger extends IDCTriggerHandler {

		NotIDCTrigger(OperableTrigger nextTrigger) {
			super(nextTrigger);
		}
		
		@Override
		void valid(Connection conn) throws SQLException {
			// do nothing
		}

		protected List<JobBarrier> computeBarriers(Connection conn) {
			return Collections.emptyList();
		}

		protected String storeJobInstance(Connection conn) {
			return IDCJobStoreTX.this.getFiredTriggerRecordId();
		}
	}

	private class MainTaskHandler extends IDCTriggerHandler {

		long shouldFireTime = -1;
		long prevFireTime = -1;
		
		MainTaskHandler(OperableTrigger trigger) {
			super(trigger);
		}
		
		@Override
		void valid(Connection conn) throws SQLException {
		}
		
		JobInstance prevIns = null;
		
		@Override
		protected List<JobBarrier> computeBarriers(Connection conn) throws SQLException {
			
			shouldFireTime = Optional.ofNullable(trigger.getNextFireTime()).map(Date::getTime).orElse(-1L);
			prevFireTime = Optional.ofNullable(trigger.getPreviousFireTime()).map(Date::getTime).orElse(-1L);
			prevIns = prevFireTime == -1 ? null : idcDriverDelegate.selectJobInstance(conn, jobKey, prevFireTime);
			
			List<JobBarrier> barriers = new ArrayList<>();
			// 流程子任务，检查上游任务是否都已完成
			// 同周期任务是否完成
			if (shouldBarrier(prevIns)) {
				barriers.add(buildBarrier(conn, jobKey, prevIns));
			}
			List<JobDependency> jobDependencies = pluginService.getJobDependencies(jobKey);
			if (!Utils.isNullOrEmpty(jobDependencies)) {
				for (JobDependency dep : jobDependencies) {
					// TODO 任务间依赖，这里应该计算数据的依赖，即 loadDate 的依赖
					JobInstance depJobIns = idcDriverDelegate.selectJobInstance(conn, dep.getDependencyJobKey(), shouldFireTime);
					if (shouldBarrier(depJobIns)) {
						barriers.add(buildBarrier(conn, jobKey, depJobIns));
					}
				}
			}
			return barriers;
		}

		@Override
		protected String storeJobInstance(Connection conn) throws SQLException {
			
			Job job = pluginService.getJob(jobKey);
			
			JobInstance ins = new JobInstance();
			// ~~ 基础信息 ~~
			ins.setTaskKey(job.getTaskKey());
			ins.setContentType(job.getContentType());
			ins.setJobKey(job.getJobKey());
			ins.setJobName(job.getJobName());
			ins.setDispatchType(job.getDispatchType());
			ins.setTaskType(job.getTaskType());
			ins.setAssignee(job.getAssignee());
			ins.setScheduleType(job.getScheduleType());
			ins.setWorkflowId(job.getWorkflowId());

			// ~~ 运行时信息 ~~
			// 批次
			ins.setShouldFireTime(shouldFireTime);
			// prev 批次
			ins.setPrevFireTime(prevFireTime);
			ins.setStartTime(LocalDateTime.now());
			ins.setEndTime(null);
			ins.setStatus(JobInstanceStatus.NEW);

			// 计算参数
			List<ExecParam> params = job.getParameter();
			if (!Utils.isNullOrEmpty(params)) {
				IDCDefaultParam dp = new IDCDefaultParam();
				dp.setShouldFireTime(IDCUtils.toLocalDateTime(ins.getShouldFireTime()));
				ParamParser parser = new ParamParser(Collections.singletonMap("idc", dp));
				parser.parse(params);
				ins.setParameter(params);
				for (ExecParam param : params) {
					if ("loadDate".equals(param.getName())) {
						ins.setLoadDate(param.getValue());
					}
				}
			}
			// loadDate
			if (ins.getLoadDate() == null) {
				ins.setLoadDate(ins.getScheduleType().format(IDCUtils.toLocalDateTime(shouldFireTime)));
			}
			return idcDriverDelegate.insertJobInstance(conn, ins).getInstanceId() + "";
		}
	}

	private class SubHandler extends IDCTriggerHandler {

		private SubEnv env;

		public SubHandler(OperableTrigger nextTrigger) {
			super(nextTrigger);
			env = JSON.parseObject(IDCContextKey.JOB_ENV.applyGet(nextTrigger.getJobDataMap()), SubEnv.class);
		}
		
		private boolean valid;
		private JobInstance mainJobIns;
		
		@Override
		void valid(Connection conn) throws SQLException {
			mainJobIns = idcDriverDelegate.selectJobInstance(conn, env.getMainInstanceId());
			valid = !mainJobIns.getStatus().isComplete();
		}

		private Long shouldFireTime = -1L;

		@Override
		protected List<JobBarrier> computeBarriers(Connection conn) throws SQLException {
			if (valid) {
				shouldFireTime = Optional.ofNullable(trigger.getNextFireTime()).map(Date::getTime).orElse(-1L);
				List<JobBarrier> barriers = new ArrayList<>();
				// 流程子任务，检查上游任务是否都已完成
				List<TaskKey> depTasks = pluginService.getPredecessors(mainJobIns.getTaskKey(), getIDCTaskKey());
				if (!Utils.isNullOrEmpty(depTasks)) {
					for (TaskKey deptk : depTasks) {
						if (deptk.equals(WorkflowEdge.START)) {
							continue;
						}
						JobKey barrierKey = IDCUtils.getSubJobKey(mainJobIns.getJobKey(), deptk);
						JobInstance barrierIns = idcDriverDelegate.selectJobInstance(conn, barrierKey, shouldFireTime);
						if (shouldBarrier(barrierIns)) {
							barriers.add(buildBarrier(conn, jobKey, barrierIns));
						}
					}
				}
				return barriers;
			}
			return Collections.emptyList();
		}

		@Override
		protected String storeJobInstance(Connection conn) throws SQLException {
			if (valid) {

				Task task = pluginService.getTask(taskKey);
				
				JobInstance ins = new JobInstance();
				// ~~ 基础信息 ~~
				ins.setJobKey(jobKey);
				ins.setJobName(task.getTaskName());
				ins.setTaskKey(taskKey);
				ins.setContentType(task.getContentType());
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
				ins.setMainInstanceId(env.getMainInstanceId());
				ins.setStartTime(LocalDateTime.now());
				ins.setEndTime(null);
				ins.setStatus(JobInstanceStatus.NEW);

				return idcDriverDelegate.insertJobInstance(conn, ins).getInstanceId() + "";
			
			} else {
				return IDCJobStoreTX.this.getFiredTriggerRecordId();
			}
		}
	}

	private class RedoHandler extends IDCTriggerHandler {

		RedoEnv env;

		RedoHandler(OperableTrigger nextTrigger) {
			super(nextTrigger);
			env = JSON.parseObject(IDCContextKey.JOB_ENV.applyGet(nextTrigger.getJobDataMap()), RedoEnv.class);
		}

		@Override
		void valid(Connection conn) throws SQLException {
		}

		@Override
		List<JobBarrier> computeBarriers(Connection conn) throws SQLException {
			return Collections.emptyList();
		}

		@Override
		String storeJobInstance(Connection conn) throws SQLException {
			JobInstance i = idcDriverDelegate.selectJobInstance(conn, env.getInstanceId());
			i.setStatus(JobInstanceStatus.NEW);
			i.setStartTime(LocalDateTime.now());
			i.setEndTime(null);
			idcDriverDelegate.updateJobInstance(conn, i);
			return env.getInstanceId() + "";
		}
	}

	private class GuardHandler extends IDCTriggerHandler {

		private GuardEnv env;
		
		public GuardHandler(OperableTrigger nextTrigger) {
			super(nextTrigger);
			env = JSON.parseObject(IDCContextKey.JOB_ENV.applyGet(nextTrigger.getJobDataMap()), GuardEnv.class);
		}

		@Override
		void valid(Connection conn) throws SQLException {
		}

		@Override
		protected List<JobBarrier> computeBarriers(Connection conn) throws SQLException {
			List<JobBarrier> barriers = new LinkedList<>();
			for (JobKey bk : env.getBarrierKeys()) {
				JobInstance barrierIns = idcDriverDelegate.selectJobInstance(conn, bk, env.getShouldFireTime());
				if (shouldBarrier(barrierIns)) {
					barriers.add(buildBarrier(conn, jobKey, barrierIns));
				}
			}
			return barriers;
		}

		@Override
		String storeJobInstance(Connection conn) throws SQLException {
			return IDCJobStoreTX.this.getFiredTriggerRecordId();
		}
	}
}