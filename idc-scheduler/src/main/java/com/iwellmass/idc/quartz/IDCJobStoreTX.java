package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.IDCUtils.toLocalDateTime;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_JSON;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_REOD;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.quartz.JobDetail;
import org.quartz.JobPersistenceException;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.spi.OperableTrigger;

import com.alibaba.fastjson.JSON;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.ParameterParser;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;

public class IDCJobStoreTX extends JobStoreTX implements IDCStore{
	
	private final IDCDriverDelegate idcDelegate;
	
	
	IDCJobStoreTX(IDCDriverDelegate idcDelegate) {
		this.idcDelegate = idcDelegate;
	}
	
	/* 生成 JobInstanceId */
	protected String getFiredTriggerRecordId() {
		return idcDelegate.nextInstanceId().toString();
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
			storeJob(conn, newTrigger);
		}
	}
	
	/* WAITING --> ACQUIRED */
	protected List<OperableTrigger> acquireNextTrigger(Connection conn, long noLaterThan, int maxCount, long timeWindow)
			throws JobPersistenceException {
		List<OperableTrigger> nextTriggers = super.acquireNextTrigger(conn, noLaterThan, maxCount, timeWindow);
		for (OperableTrigger trigger : nextTriggers) {
			/////////////////////////////
			// 保存 JobInstance & 阻塞下一个任务
			////////////////////////////
			JobInstance ins = storeJobInstance(conn, trigger);
			try {
				JobBarrier selfBarrier = buildSeqBarriers(ins);
				idcDelegate.batchInsertJobBarrier(conn, Collections.singletonList(selfBarrier));
			} catch (SQLException e) {
				throw new JobPersistenceException("保存 Self-JobBarrier 时出错: " + e.getMessage(), e);
			}
		}
		return nextTriggers;
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
	
	private List<JobBarrier> buildDependencyBarriers() {
		return Collections.emptyList(); // TODO try finished it
	}
	
	private void storeJob(Connection conn, OperableTrigger trigger) throws JobPersistenceException {
		try {
			Job idcJob = JSON.parseObject(JOB_JSON.applyGet(trigger.getJobDataMap()), Job.class);
			idcDelegate.insertJob(conn, idcJob);
			/////////////////////////////
			// 解析依赖填充 barrier 信息
			/////////////////////////////
			idcDelegate.clearJobBarrier(conn, idcJob.getJobId(), idcJob.getJobGroup());
			List<Job> deps = idcDelegate.selectJobDependencies(conn, idcJob);
			if (!Utils.isNullOrEmpty(deps)) {
				List<JobBarrier> barriers = buildDependencyBarriers();
				idcDelegate.batchInsertJobBarrier(conn, barriers);
			}
		} catch (SQLException e) {
			throw new JobPersistenceException("保存 Job 信息是出错: " + e.getMessage(), e);
		}
	}
	
	private JobInstance storeJobInstance(Connection conn, OperableTrigger trigger) throws JobPersistenceException {
		try {
			////////////////////////////////
			// 保存 JobInstance ( TODO 从数据库中取？)
			////////////////////////////////
			Job idcJob = JSON.parseObject(JOB_JSON.applyGet(trigger.getJobDataMap()), Job.class);
			Boolean isRedo = JOB_REOD.applyGet(trigger.getJobDataMap());
			
			ParameterParser parser = IDCContextKey.JOB_PARAMETER_PARSER.applyGet(trigger.getJobDataMap());
			String contextParameter = IDCContextKey.CONTEXT_PARAMETER.applyGet(trigger.getJobDataMap());
			idcJob.setParameter(parser.parse(idcJob.getParameter(), contextParameter));
			
			Integer instanceId = Integer.parseInt(trigger.getFireInstanceId()); // TODO think about it
			
			if (isRedo) {
				return idcDelegate.updateJobInstance(conn, instanceId, (ins) -> {
					ins.setStartTime(LocalDateTime.now());
					ins.setEndTime(null);
					ins.setStatus(JobInstanceStatus.NEW);
					ins.setParameter(parser.parse(idcJob.getParameter(), contextParameter));
				});
			} else {
				JobInstance newIns = new JobInstance();
				// 基本信息
				newIns.setInstanceId(instanceId);
				newIns.setJobId(idcJob.getJobId());
				newIns.setJobGroup(idcJob.getJobGroup());
				newIns.setTaskId(idcJob.getTaskId());
				newIns.setGroupId(idcJob.getGroupId());
				newIns.setTaskName(idcJob.getTaskName());
				newIns.setDescription(idcJob.getDescription());
				newIns.setContentType(idcJob.getContentType());
				newIns.setTaskType(idcJob.getTaskType());
				newIns.setAssignee(idcJob.getAssignee());
				newIns.setParameter(idcJob.getParameter());
				newIns.setScheduleType(idcJob.getScheduleType());
				newIns.setStartTime(LocalDateTime.now());
				newIns.setEndTime(null);
				newIns.setStatus(JobInstanceStatus.NEW);
				newIns.setInstanceType(idcJob.getDispatchType());
				// 参数
				// 其他参数
				if (newIns.getDispatchType() == DispatchType.MANUAL) {
					LocalDateTime loadDate = CONTEXT_LOAD_DATE.applyGet(trigger.getJobDataMap());
					newIns.setLoadDate(loadDate);
					newIns.setNextLoadDate(null);
					newIns.setShouldFireTime(IDCUtils.toEpochMilli(loadDate));
				} else {
					Date shouldFireTime = trigger.getNextFireTime();
					LocalDateTime loadDate = toLocalDateTime(shouldFireTime);
					newIns.setLoadDate(loadDate);
					newIns.setNextLoadDate(toLocalDateTime(trigger.getPreviousFireTime()));
					newIns.setShouldFireTime(shouldFireTime == null ? -1 : shouldFireTime.getTime());
				}
				return idcDelegate.insertJobInstance(conn, newIns);
			}
		} catch (SQLException e) {
			throw new JobPersistenceException("保存 JobInstance 时出错: " + e.getMessage(), e);
		}
	}

	@Override
	public JobInstance retrieveIDCJobInstance(Integer instanceId) {
		try {
			return executeWithoutLock(new TransactionCallback<JobInstance>() {
				@Override
				public JobInstance execute(Connection conn) throws JobPersistenceException {
					try {
						return idcDelegate.selectJobInstance(conn, instanceId);
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
	public Job retrieveIDCJob(JobKey jobKey) {
		try {
			return executeWithoutLock(new TransactionCallback<Job>() {
				@Override
				public Job execute(Connection conn) throws JobPersistenceException {
					try {
						return idcDelegate.selectJob(conn, jobKey);
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
						idcDelegate.updateJobInstance(conn, instanceId, func);
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
	public void removeIDCJobBarriers(String barrierId, String barrierGroup, Long shouldFireTime) {
		try {
			executeInLock(LOCK_TRIGGER_ACCESS, new TransactionCallback<Void>() {
				@Override
				public Void execute(Connection conn) throws JobPersistenceException {
					try {
						idcDelegate.deleteBarriers(conn, barrierId, barrierGroup, shouldFireTime);
						signalSchedulingChangeOnTxCompletion(0L);
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
	public JobInstance retrieveIDCJobInstance(JobKey jobKey, Long shouldFireTime) {
		try {
			return executeWithoutLock(new TransactionCallback<JobInstance>() {
				@Override
				public JobInstance execute(Connection conn) throws JobPersistenceException {
					try {
						return idcDelegate.selectJobInstance(conn, jobKey, shouldFireTime);
					} catch (SQLException e) {
						throw new JobPersistenceException(e.getMessage(), e);
					}
				}
			});
		} catch (JobPersistenceException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

}