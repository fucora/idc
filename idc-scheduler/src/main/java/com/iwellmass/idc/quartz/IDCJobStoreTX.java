package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.IDCUtils.toLocalDateTime;
import static com.iwellmass.idc.quartz.IDCContextKey.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.quartz.JobDetail;
import org.quartz.JobPersistenceException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.spi.OperableTrigger;

import com.alibaba.fastjson.JSON;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.ParameterParser;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.BarrierState;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.ScheduleEnv;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.ScheduleEnv;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.Task;

public class IDCJobStoreTX extends JobStoreTX implements IDCJobStore {
	
	private final IDCDriverDelegate idcDriverDelegate;
	
	IDCJobStoreTX(IDCDriverDelegate idcDelegate) {
		this.idcDriverDelegate = idcDelegate;
	}
	
	/* 生成 JobInstanceId */
	protected String getFiredTriggerRecordId() {
		return idcDriverDelegate.nextInstanceId().toString();
	}

	/* NONE --> WAITING */
	protected void storeTrigger(Connection conn, OperableTrigger newTrigger, JobDetail job, boolean replaceExisting,
			String state, boolean forceState, boolean recovering) throws JobPersistenceException {
		boolean exists = triggerExists(conn, newTrigger.getKey());
		super.storeTrigger(conn, newTrigger, job, replaceExisting, state, forceState, recovering);
		/////////////////////////////
		// 保存 Job 信息
		/////////////////////////////
		if (!exists) {
			storeJob(conn, newTrigger, job);
		}
	}
	
	/* WAITING --> ACQUIRED */
	protected List<OperableTrigger> acquireNextTrigger(Connection conn, long noLaterThan, int maxCount, long timeWindow)
			throws JobPersistenceException {
		List<OperableTrigger> nextTriggers = super.acquireNextTrigger(conn, noLaterThan, maxCount, timeWindow);
		for (OperableTrigger trigger : nextTriggers) {
			/////////////////////////////
			// 保存 JobInstance & 周期阻塞
			////////////////////////////
			JobInstance ins = storeJobInstance(conn, trigger);
			try {
				JobBarrier selfBarrier = buildSeqBarriers(ins);
				idcDriverDelegate.batchInsertJobBarrier(conn, Collections.singletonList(selfBarrier));
			} catch (SQLException e) {
				throw new JobPersistenceException("保存 SelfJobBarrier 时出错: " + e.getMessage(), e);
			}
		}
		return nextTriggers;
	}
	
	private void storeJob(Connection conn, OperableTrigger trigger, JobDetail job) throws JobPersistenceException {
		try {
			Task task = JSON.parseObject(TASK_JSON.applyGet(job.getJobDataMap()), Task.class);
			ScheduleProperties schdProps = JSON.parseObject(JOB_SCHEDULE_PROPERTIES.applyGet(trigger.getJobDataMap()), ScheduleProperties.class);
			
			Job idcJob = new Job();
			idcJob.setTaskId(task.getTaskId());
			idcJob.setGroupId(task.getTaskGroup());
			idcJob.setContentType(task.getContentType());
			idcJob.setCreateTime(LocalDateTime.now());
			idcJob.setDescription(task.getDescription());
			idcJob.setDispatchType(task.getDispatchType());
			idcJob.setJobGroup(task.getTaskGroup());
			idcJob.setJobId(task.getTaskId());
			idcJob.setTaskType(task.getTaskType());
			idcJob.setTaskName(task.getTaskName());
			idcJob.setTaskType(task.getTaskType());
			idcJob.setWorkflowId(task.getWorkflowId());
			idcJob.setUpdateTime(null);
			
			// TODO 任务间依赖 
			// job.setDependencies(null);

			// 调度属性
			idcJob.setAssignee(schdProps.getAssignee());
			idcJob.setParameter(schdProps.getParameter());
			idcJob.setScheduleProperties(schdProps);
			idcJob.setScheduleType(schdProps.getScheduleType());
			
			idcDriverDelegate.insertJob(conn, idcJob);
			/////////////////////////////
			// 解析依赖填充 barrier 信息
			/////////////////////////////
			idcDriverDelegate.clearJobBarrier(conn, idcJob.getJobId(), idcJob.getJobGroup());
			List<JobBarrier> barriers = buildDependencyBarriers(conn, trigger, idcJob.getJobKey());
			if (!Utils.isNullOrEmpty(barriers)) {
				idcDriverDelegate.batchInsertJobBarrier(conn, barriers);
			}
		} catch (SQLException e) {
			throw new JobPersistenceException("保存 Job 信息是出错: " + e.getMessage(), e);
		}
	}
	
	private JobInstance storeJobInstance(Connection conn, OperableTrigger trigger) throws JobPersistenceException {
		try {
			ScheduleEnv schdEnv = initScheduleEnv(trigger);
			////////////////////////////////
			// 保存 JobInstance
			////////////////////////////////
			
			Job idcJob = idcDriverDelegate.selectJob(conn, IDCUtils.parseJobKey(trigger));
			JobInstance newIns = new JobInstance();
			// ~~ 基本信息 ~~
			newIns.setJobId(idcJob.getJobId());
			newIns.setJobGroup(idcJob.getJobGroup());
			newIns.setTaskId(idcJob.getTaskId());
			newIns.setGroupId(idcJob.getGroupId());
			newIns.setTaskName(idcJob.getTaskName());
			newIns.setDescription(idcJob.getDescription());
			newIns.setContentType(idcJob.getContentType());
			newIns.setTaskType(idcJob.getTaskType());
			newIns.setAssignee(idcJob.getAssignee());
			newIns.setScheduleType(idcJob.getScheduleType());
			newIns.setInstanceType(idcJob.getDispatchType());
			newIns.setWorkflowId(idcJob.getWorkflowId());

			// ~~ 运行时信息 ~~
			// instance id
			newIns.setInstanceId(schdEnv.getInstanceId());
			// 执行参数
			newIns.setParameter(schdEnv.getParameter());
			// 批次
			newIns.setShouldFireTime(schdEnv.getShouldFireTime());
			// 其他
			newIns.setStartTime(LocalDateTime.now());
			newIns.setEndTime(null);
			newIns.setStatus(JobInstanceStatus.NEW);
			return idcDriverDelegate.insertJobInstance(conn, newIns);
		} catch (SQLException e) {
			throw new JobPersistenceException("保存 JobInstance 时出错: " + e.getMessage(), e);
		}
	}
	
	private ScheduleEnv initScheduleEnv(OperableTrigger trigger) {
		ScheduleProperties props = JSON.parseObject(JOB_SCHEDULE_PROPERTIES.applyGet(trigger.getJobDataMap()), ScheduleProperties.class);
		ScheduleEnv schdEnv = JSON.parseObject(JOB_SCHEDULE_ENV.applyGet(trigger.getJobDataMap()), ScheduleEnv.class);
		if (schdEnv == null) {
			schdEnv = new ScheduleEnv();
		}
		// id
		if (schdEnv.getInstanceId() == null) {
			schdEnv.setInstanceId(Integer.parseInt(trigger.getFireInstanceId()));
		}
		// should-fire-time
		if (schdEnv.getShouldFireTime() == null) {
			schdEnv.setShouldFireTime(trigger.getNextFireTime().getTime());
		}
		// parameter
		ParameterParser parser = IDCContextKey.JOB_PARAMETER_PARSER.applyGet(trigger.getJobDataMap());
		schdEnv.setParameter(parser.parse(props.getParameter(), schdEnv.getParameter()));
		// TODO something else
		return schdEnv;
	}
	
	private JobBarrier buildSeqBarriers(JobInstance ins) {
		JobBarrier barrier = new JobBarrier();
		barrier.setJobGroup(ins.getJobGroup());
		barrier.setJobId(ins.getJobId());
		barrier.setBarrierGroup(ins.getJobGroup());
		barrier.setBarrierId(ins.getJobId());
		barrier.setShouldFireTime(ins.getShouldFireTime());
		return barrier;
	}
	
	private List<JobBarrier> buildDependencyBarriers(Connection conn, Trigger trigger, JobKey jobKey) throws SQLException {
		List<JobDependency> jobDependencies = idcDriverDelegate.selectJobDependencies(conn, jobKey);

		if (Utils.isNullOrEmpty(jobDependencies)) {
			return Collections.emptyList();
		} else {
			return jobDependencies.stream()
			// 计算 shouldFireTime
			.map(dep -> {
				JobDependencyInstance r = new JobDependencyInstance();
				r.jobKey = dep.getDependencyJobKey();
				r.shouldFireTime = trigger.getNextFireTime().getTime();
				return r;
			})
			// 过滤未完成的任务
			.filter(check -> idcDriverDelegate.countSuccessor(check.jobKey, check.shouldFireTime) < 1)
			// 构建 barrier
			.map(b -> {
				JobBarrier barrier = new JobBarrier();
				barrier.setBarrierKey(b.jobKey);
				barrier.setJobKey(jobKey);
				barrier.setShouldFireTime(b.shouldFireTime);
				barrier.setState(BarrierState.VALID);
				return barrier;
			}).collect(Collectors.toList());
		}
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
	public Job retrieveIDCJob(JobKey jobKey) throws JobPersistenceException{
		return executeWithoutLock(new TransactionCallback<Job>() {
			@Override
			public Job execute(Connection conn) throws JobPersistenceException {
				try {
					return idcDriverDelegate.selectJob(conn, jobKey);
				} catch (SQLException e) {
					throw new JobPersistenceException(e.getMessage(), e);
				}
			}
		});
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
	public JobInstance retrieveIDCJobInstance(JobKey jobKey, Long shouldFireTime) throws JobPersistenceException {
		return executeWithoutLock(new TransactionCallback<JobInstance>() {
			@Override
			public JobInstance execute(Connection conn) throws JobPersistenceException {
				try {
					return idcDriverDelegate.selectJobInstance(conn, jobKey, shouldFireTime);
				} catch (SQLException e) {
					throw new JobPersistenceException(e.getMessage(), e);
				}
			}
		});
	}
	
	@Override
	public void completeIDCJobInstance(CompleteEvent event) throws JobPersistenceException {
		executeInLock(null, new TransactionCallback<Void>() {
			@Override
			public Void execute(Connection conn) throws JobPersistenceException {
				completeIDCJobInstance(conn, event);
				return null;
			}
		});
		
	}
	
	protected void completeIDCJobInstance(Connection conn, CompleteEvent event) throws JobPersistenceException {
		try {
			JobInstance ins = idcDriverDelegate.selectJobInstance(conn, event.getInstanceId());
			
			if (ins == null) {
				getLog().warn("任务 {} 不存在", event.getInstanceId());
				return;
			}
			
			// 获取任务
			JobKey jobKey = ins.getJobKey();
			Job job = idcDriverDelegate.selectJob(conn, ins.getJobKey());
			ScheduleProperties sp = job.getScheduleProperties();
			
			// 删除 barrier
			if (!sp.getBlockOnError() ||
					event.getFinalStatus() == JobInstanceStatus.FINISHED) {
				idcDriverDelegate.deleteBarriers(conn, jobKey.getJobId(), jobKey.getJobGroup(), ins.getShouldFireTime());
			}
			
			signalSchedulingChangeOnTxCompletion(0L);
		} catch (SQLException e) {
			throw new JobPersistenceException(e.getMessage(), e);
		}
	}
	
	private class JobDependencyInstance {
		JobKey jobKey;
		long shouldFireTime;
	}
}