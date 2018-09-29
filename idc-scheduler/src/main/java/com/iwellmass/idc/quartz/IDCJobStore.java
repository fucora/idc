package com.iwellmass.idc.quartz;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.quartz.JobDetail;
import org.quartz.JobPersistenceException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.jdbcjobstore.TriggerStatus;
import org.quartz.spi.OperableTrigger;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.JobInstanceStatus;

public class IDCJobStore extends JobStoreTX {
	
	// waiting on async result
	public static final String STATE_ASYNC_WAITING = "ASYNC_WAITING";
	// job complete
	public static final String STATE_ASYNC_COMPLETE = "ASYNC_COMPLETE";
	
	@Override
	protected void triggeredJobComplete(Connection conn, OperableTrigger trigger, JobDetail jobDetail,
			CompletedExecutionInstruction triggerInstCode) throws JobPersistenceException {
        try {
            if (triggerInstCode == CompletedExecutionInstruction.DELETE_TRIGGER) {
                if(trigger.getNextFireTime() == null) { 
                    // double check for possible reschedule within job 
                    // execution, which would cancel the need to delete...
                    TriggerStatus stat = getDelegate().selectTriggerStatus(
                            conn, trigger.getKey());
                    if(stat != null && stat.getNextFireTime() == null) {
                        removeTrigger(conn, trigger.getKey());
                    }
                } else{
                    removeTrigger(conn, trigger.getKey());
                    signalSchedulingChangeOnTxCompletion(0L);
                }
            } else if (triggerInstCode == CompletedExecutionInstruction.SET_TRIGGER_COMPLETE) {
                getDelegate().updateTriggerState(conn, trigger.getKey(),
                        STATE_COMPLETE);
                signalSchedulingChangeOnTxCompletion(0L);
            } else if (triggerInstCode == CompletedExecutionInstruction.SET_TRIGGER_ERROR) {
                getLog().info("Trigger " + trigger.getKey() + " set to ERROR state.");
                getDelegate().updateTriggerState(conn, trigger.getKey(),
                        STATE_ERROR);
                signalSchedulingChangeOnTxCompletion(0L);
            } else if (triggerInstCode == CompletedExecutionInstruction.SET_ALL_JOB_TRIGGERS_COMPLETE) {
                getDelegate().updateTriggerStatesForJob(conn,
                        trigger.getJobKey(), STATE_COMPLETE);
                signalSchedulingChangeOnTxCompletion(0L);
            } else if (triggerInstCode == CompletedExecutionInstruction.SET_ALL_JOB_TRIGGERS_ERROR) {
                getLog().info("All triggers of Job " + 
                        trigger.getKey() + " set to ERROR state.");
                getDelegate().updateTriggerStatesForJob(conn,
                        trigger.getJobKey(), STATE_ERROR);
                signalSchedulingChangeOnTxCompletion(0L);
            }

            /*
             QZ 原逻辑，使用异步接口通知
             if (jobDetail.isConcurrentExectionDisallowed()) {
                getDelegate().updateTriggerStatesForJobFromOtherState(conn,
                        jobDetail.getKey(), STATE_WAITING,
                        STATE_BLOCKED);

                getDelegate().updateTriggerStatesForJobFromOtherState(conn,
                        jobDetail.getKey(), STATE_PAUSED,
                        STATE_PAUSED_BLOCKED);

                signalSchedulingChangeOnTxCompletion(0L);
            }*/
            
            if (jobDetail.isPersistJobDataAfterExecution()) {
                try {
                    if (jobDetail.getJobDataMap().isDirty()) {
                        getDelegate().updateJobData(conn, jobDetail);
                    }
                } catch (IOException e) {
                    throw new JobPersistenceException(
                            "Couldn't serialize job data: " + e.getMessage(), e);
                } catch (SQLException e) {
                    throw new JobPersistenceException(
                            "Couldn't update job data: " + e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            throw new JobPersistenceException(
                    "Couldn't update trigger state(s): " + e.getMessage(), e);
        }

        try {
            getDelegate().deleteFiredTrigger(conn, trigger.getFireInstanceId());
        } catch (SQLException e) {
            throw new JobPersistenceException("Couldn't delete fired trigger: "
                    + e.getMessage(), e);
        }
    }
	
	
	public void triggeredAsyncJobComplete(TriggerKey triggerKey, CompleteEvent event)
			throws JobPersistenceException {
        retryExecuteInNonManagedTXLock(
                null,
                new TransactionCallback<Void>() {
                    public Void execute(Connection conn) throws JobPersistenceException {
                    	triggeredAsyncJobComplete(conn, triggerKey, event);
                        return null;
                    }
                });    
	}

	protected void triggeredAsyncJobComplete(Connection conn, TriggerKey key, CompleteEvent event) throws JobPersistenceException {
		
		if (event.getEndTime() == null) {
			event.setEndTime(LocalDateTime.now());
		}
        try {
        	
        	/* 无论什么情况我们都更新 IDC，因为重跑、重报 都希望我们更新界面上看到的任务状态*/ 
     		if (getDelegate() instanceof IDCDriverDelegate) {
    			IDCDriverDelegate delegate = (IDCDriverDelegate) getDelegate();
        		delegate.updateTriggerStateForIDC(conn, event);	
        		signalSchedulingChangeOnTxCompletion(0L);
    		}
        	
        	Trigger trigger = getDelegate().selectTrigger(conn, key);
        	
        	if (trigger == null ) {
        		getLog().warn("不存在的 trigger 信息");
        		return;
        	}
        	
        	if (trigger.getPreviousFireTime() != null && trigger.getPreviousFireTime().getTime() != event.getScheduledFireTime()) {
        		getLog().warn("未更新 Trigger, 请求{}, 当前 {}", event.getScheduledFireTime(), trigger.getNextFireTime().getTime());
        		return;
        	}
        	
        	if (event.getFinalStatus() == JobInstanceStatus.FINISHED) {
        		getDelegate().updateTriggerStateFromOtherState(conn,
        				key, STATE_WAITING,
        				STATE_BLOCKED);
        		
        		getDelegate().updateTriggerStateFromOtherState(conn,
        				key, STATE_PAUSED,
        				STATE_PAUSED_BLOCKED);
        		signalSchedulingChangeOnTxCompletion(0L);
        	} else if (event.getFinalStatus() == JobInstanceStatus.FAILED) {
        		// do nothing
        		signalSchedulingChangeOnTxCompletion(0L);
        	} else {
        		throw new UnsupportedOperationException("unsupported event status " + event.getFinalStatus());
        	}
        } catch (Exception e) {
            throw new JobPersistenceException("Couldn't resume async job, trigger '"
                    + key + "': " + e.getMessage(), e);
        }
	}
	
}
