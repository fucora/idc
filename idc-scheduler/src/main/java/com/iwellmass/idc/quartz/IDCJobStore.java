package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.JOB_ASYNC;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.quartz.JobDetail;
import org.quartz.JobPersistenceException;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.jdbcjobstore.TriggerStatus;
import org.quartz.spi.OperableTrigger;

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

            if (jobDetail.isConcurrentExectionDisallowed()) {
            	// async job ?
            	boolean isAsync = JOB_ASYNC.applyGet(jobDetail.getJobDataMap());
            	if (isAsync) {
            		// update to WAITING_ASYNC 
            		getDelegate().updateTriggerStatesForJobFromOtherState(conn,
            				jobDetail.getKey(), STATE_ASYNC_WAITING,
            				STATE_BLOCKED);
            		// may be early notify
        			getDelegate().updateTriggerStatesForJobFromOtherState(conn,
            				jobDetail.getKey(), STATE_WAITING,
            				STATE_ASYNC_COMPLETE);
            	} else {
            		getDelegate().updateTriggerStatesForJobFromOtherState(conn,
            				jobDetail.getKey(), STATE_WAITING,
            				STATE_BLOCKED);
	                getDelegate().updateTriggerStatesForJobFromOtherState(conn,
	                        jobDetail.getKey(), STATE_PAUSED,
	                        STATE_PAUSED_BLOCKED);
            	}

                signalSchedulingChangeOnTxCompletion(0L);
            }
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
	
	
	public void triggeredAsyncJobComplete(TriggerKey triggerKey, CompletedExecutionInstruction triggerInstCode) throws JobPersistenceException {
        executeInLock(
                LOCK_TRIGGER_ACCESS,
                new TransactionCallback<Object>() {
                    public Object execute(Connection conn) throws JobPersistenceException {
                    	triggeredAsyncJobComplete(conn, triggerKey, triggerInstCode);
                        return null;
                    }
                });
    }
	
	protected void triggeredAsyncJobComplete(Connection conn, TriggerKey key, CompletedExecutionInstruction triggerInstCode) throws JobPersistenceException {
        try {
            TriggerStatus status = getDelegate().selectTriggerStatus(conn, key);

            if (status == null || status.getNextFireTime() == null) {
                return;
            }
            
            // job complete
            if (triggerInstCode == CompletedExecutionInstruction.NOOP) {
            	getDelegate().updateTriggerStateFromOtherState(conn, key, STATE_COMPLETE, STATE_ASYNC_WAITING);
            	getDelegate().updateTriggerStateFromOtherState(conn, key, STATE_ASYNC_COMPLETE, STATE_BLOCKED);
            	signalSchedulingChangeOnTxCompletion(0L);
            } else {
            	getDelegate().updateTriggerStateFromOtherState(conn, key, STATE_ERROR, STATE_ASYNC_WAITING);
            	// TODO check loadDate ?
            	getDelegate().updateTriggerStateFromOtherState(conn, key, STATE_ERROR, STATE_BLOCKED);
            	signalSchedulingChangeOnTxCompletion(0L);
            }
        } catch (SQLException e) {
            throw new JobPersistenceException("Couldn't resume async job, trigger '"
                    + key + "': " + e.getMessage(), e);
        }
	}
	
}
