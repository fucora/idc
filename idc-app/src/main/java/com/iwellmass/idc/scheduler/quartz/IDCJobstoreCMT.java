package com.iwellmass.idc.scheduler.quartz;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.JobPersistenceException;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.JobStoreCMT;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.Setter;

	public class IDCJobstoreCMT extends JobStoreCMT implements IDCJobStore {

	@Setter
	private int parallelMax = 2;

	@Setter
	private RecordIdGenerator recordIdGenerator;

	public IDCJobstoreCMT() {
		this.recordIdGenerator = ()->super.getFiredTriggerRecordId();
	}
	
	/*
	 * 自定义 entry id
	 */
	@Override
	public synchronized String getFiredTriggerRecordId() {
		return recordIdGenerator.generate();
	}

	/*
	 * 并发控制
	 */
	@Override
	protected List<OperableTrigger> acquireNextTrigger(Connection conn, long noLaterThan, int maxCount, long timeWindow)
			throws JobPersistenceException {
		int acceptCount = parallelMax;
		// todo 查询实际执行的job有哪些. -> runningJobs
		int runningJobs = 0;
		acceptCount = parallelMax - runningJobs;

		return acceptCount > 0
				? super.acquireNextTrigger(conn, noLaterThan, Math.min(maxCount, acceptCount), timeWindow)
				: Collections.emptyList();
	}
	
	/*
	 * 扩展 SUSPEND 机制
	 */
	@Override
	protected TriggerFiredBundle triggerFired(Connection conn, OperableTrigger trigger) throws JobPersistenceException {
		TriggerFiredBundle bundle = super.triggerFired(conn, trigger);
		if (bundle != null) {
			// 是否设置 SUSPEND 状态
			boolean suspend = isSuspendAfterExecution(bundle.getJobDetail());
			if (suspend) {
				try {
					getDelegate().updateTriggerStateFromOtherState(conn, trigger.getKey(), STATE_SUSPENDED, STATE_BLOCKED);
					getDelegate().updateTriggerStateFromOtherState(conn, trigger.getKey(), STATE_PAUSED_SUSPENDED, STATE_PAUSED_BLOCKED);
					signalSchedulingChangeOnTxCompletion(0L);
				} catch (SQLException e) {
					throw new JobPersistenceException(e.getMessage(), e);
				}
			}
		}
		// 事务提交
		return bundle;
	}
	
	@Override
	public void triggeredJobComplete(OperableTrigger trigger, JobDetail jobDetail,
			CompletedExecutionInstruction triggerInstCode) {
		// 事务退出
		super.triggeredJobComplete(trigger, jobDetail, triggerInstCode);
		
		// 通知线程启动
//		String batchNo = trigger.getFireInstanceId();
//		if (batchNo != null) {
//			boolean isAsyncTask = Job.isAsyncTask(jobDetail);
//			if (isAsyncTask) {
//				TaskEventService eventService = TaskEventPlugin.eventService(instanceName);
//				StartMessage message = StartMessage.newMessage(batchNo);
//				eventService.send(message);
//			}
//		}
	}
	
	@Override
	public void pauseTrigger(Connection conn, TriggerKey triggerKey) throws JobPersistenceException {
		super.pauseTrigger(conn, triggerKey);
		// LOCKED -> LOCKED_PAUSE
		try {
			getDelegate().updateTriggerStateFromOtherState(conn, triggerKey, STATE_PAUSED_SUSPENDED, STATE_SUSPENDED);
		} catch (SQLException e) {
			throw new JobPersistenceException(e.getMessage(), e);
		}
	}

	@Override
	public void resumeTrigger(Connection conn, TriggerKey key) throws JobPersistenceException {
		super.resumeTrigger(conn, key);
	}
	

	/*
	 * RELEASE 操作
	 * 
	 * @param triggerKey 计划
	 * 
	 * @param entryId 实例
	 */
	public void releaseTrigger(TriggerKey triggerKey, ReleaseInstruction inst) {
		retryExecuteInNonManagedTXLock(LOCK_TRIGGER_ACCESS, (conn) -> {
			try {
				if (inst == ReleaseInstruction.SET_ERROR) {
					getDelegate().updateTriggerState(conn, triggerKey, STATE_ERROR);
				} else if (inst == ReleaseInstruction.RELEASE) {
					getDelegate().updateTriggerStateFromOtherState(conn, triggerKey, STATE_WAITING, STATE_SUSPENDED);
					getDelegate().updateTriggerStateFromOtherState(conn, triggerKey, STATE_PAUSED, STATE_PAUSED_SUSPENDED);
				}
				signalSchedulingChangeOnTxCompletion(0L);
			} catch (SQLException e) {
				throw new JobPersistenceException(e.getMessage(), e);
			}
			return null;
		});
	}

	@Override
	protected void closeConnection(Connection conn) {
		boolean springManaged = TransactionSynchronizationManager.isActualTransactionActive();
		if (!springManaged) {
			super.closeConnection(conn);
		}
	}
}
