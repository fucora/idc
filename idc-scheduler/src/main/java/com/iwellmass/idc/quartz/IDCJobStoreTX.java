package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE_ID;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_JSON;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_REOD;
import static com.iwellmass.idc.quartz.IDCUtils.toLocalDateTime;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Consumer;

import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.JobPersistenceException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.DriverDelegate;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.jdbcjobstore.NoSuchDelegateException;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredBundle;

import com.alibaba.fastjson.JSON;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;

public class IDCJobStoreTX extends JobStoreTX {

	public void storeIdcJob(Job job) throws JobPersistenceException {
		executeInLock(null, new TransactionCallback<Void>() {
			@Override
			public Void execute(Connection conn) throws JobPersistenceException {
				getIDCDriverDelegate().insertIDCJob(conn, job);
				return null;
			}
		});
	}
	
	// NONE --> WAITING
	@Override
	protected void storeTrigger(Connection conn, OperableTrigger newTrigger, JobDetail job, boolean replaceExisting,
			String state, boolean forceState, boolean recovering) throws JobPersistenceException {

        boolean existingTrigger = triggerExists(conn, newTrigger.getKey());

        if ((existingTrigger) && (!replaceExisting)) { 
            throw new ObjectAlreadyExistsException(newTrigger); 
        }
        
        try {

            boolean shouldBepaused;

            if (!forceState) {
                shouldBepaused = getDelegate().isTriggerGroupPaused(
                        conn, newTrigger.getKey().getGroup());

                if(!shouldBepaused) {
                    shouldBepaused = getDelegate().isTriggerGroupPaused(conn,
                            ALL_GROUPS_PAUSED);

                    if (shouldBepaused) {
                        getDelegate().insertPausedTriggerGroup(conn, newTrigger.getKey().getGroup());
                    }
                }

                if (shouldBepaused && (state.equals(STATE_WAITING) || state.equals(STATE_ACQUIRED))) {
                    state = STATE_PAUSED;
                }
            }

            if(job == null) {
                job = retrieveJob(conn, newTrigger.getJobKey());
            }
            if (job == null) {
                throw new JobPersistenceException("The job ("
                        + newTrigger.getJobKey()
                        + ") referenced by the trigger does not exist.");
            }

            if (job.isConcurrentExectionDisallowed() && !recovering) { 
                state = checkBlockedState(conn, job.getKey(), state);
            }
            
            if (existingTrigger) {
                getDelegate().updateTrigger(conn, newTrigger, state, job);
            } else {
                getDelegate().insertTrigger(conn, newTrigger, state, job);
                ///////////////////////////////////////////////////////////
                // 同步创建 Job
                if (!recovering) {
                	String jobJson = JOB_JSON.applyGet(newTrigger.getJobDataMap());
                	if (jobJson != null) {
                		Job idcJob = JSON.parseObject(jobJson, Job.class);
                		getIDCDriverDelegate().insertIDCJob(conn, idcJob);
                	}
                }
                ////////////////////////////////////////////////////////////
            }
        } catch (Exception e) {
            throw new JobPersistenceException("Couldn't store trigger '" + newTrigger.getKey() + "' for '" 
                    + newTrigger.getJobKey() + "' job:" + e.getMessage(), e);
        }
    }
	
	// WAITING --> ACQUIRED
	@Override
	protected TriggerFiredBundle triggerFired(Connection conn, OperableTrigger trigger) throws JobPersistenceException {
        JobDetail job;
        Calendar cal = null;

        // Make sure trigger wasn't deleted, paused, or completed...
        try { // if trigger was deleted, state will be STATE_DELETED
            String state = getDelegate().selectTriggerState(conn,
                    trigger.getKey());
            if (!state.equals(STATE_ACQUIRED)) {
                return null;
            }
        } catch (SQLException e) {
            throw new JobPersistenceException("Couldn't select trigger state: "
                    + e.getMessage(), e);
        }

        try {
            job = retrieveJob(conn, trigger.getJobKey());
            if (job == null) { return null; }
        } catch (JobPersistenceException jpe) {
            try {
                getLog().error("Error retrieving job, setting trigger state to ERROR.", jpe);
                getDelegate().updateTriggerState(conn, trigger.getKey(),
                        STATE_ERROR);
            } catch (SQLException sqle) {
                getLog().error("Unable to set trigger state to ERROR.", sqle);
            }
            throw jpe;
        }

        if (trigger.getCalendarName() != null) {
            cal = retrieveCalendar(conn, trigger.getCalendarName());
            if (cal == null) { return null; }
        }

        try {
            getDelegate().updateFiredTrigger(conn, trigger, STATE_EXECUTING, job);
        } catch (SQLException e) {
            throw new JobPersistenceException("Couldn't insert fired trigger: "
                    + e.getMessage(), e);
        }

        Date prevFireTime = trigger.getPreviousFireTime();

        // call triggered - to update the trigger's next-fire-time state...
        trigger.triggered(cal);

        String state = STATE_WAITING;
        boolean force = true;
        
        if (job.isConcurrentExectionDisallowed()) {
            state = STATE_BLOCKED;
            force = false;
            try {
                getDelegate().updateTriggerStatesForJobFromOtherState(conn, job.getKey(),
                        STATE_BLOCKED, STATE_WAITING);
                getDelegate().updateTriggerStatesForJobFromOtherState(conn, job.getKey(),
                        STATE_BLOCKED, STATE_ACQUIRED);
                getDelegate().updateTriggerStatesForJobFromOtherState(conn, job.getKey(),
                        STATE_PAUSED_BLOCKED, STATE_PAUSED);
            } catch (SQLException e) {
                throw new JobPersistenceException(
                        "Couldn't update states of blocked triggers: "
                                + e.getMessage(), e);
            }
        } 
            
        if (trigger.getNextFireTime() == null) {
            state = STATE_COMPLETE;
            force = true;
        }

        storeTrigger(conn, trigger, job, true, state, force, false);

        ///////////////////////////////////////////
        // 生成 instance 记录
        JobInstance v = storeIDCJobInstance(conn, trigger);
        JOB_INSTANCE.applyPut(trigger.getJobDataMap(), v);
        ///////////////////////////////////////////
        
        job.getJobDataMap().clearDirtyFlag();

        return new TriggerFiredBundle(job, trigger, cal, trigger.getKey().getGroup()
                .equals(Scheduler.DEFAULT_RECOVERY_GROUP), new Date(), trigger
                .getPreviousFireTime(), prevFireTime, trigger.getNextFireTime());
    }
	
	protected JobInstance storeIDCJobInstance(Connection conn, OperableTrigger trigger) throws JobPersistenceException {
		Boolean isRedo = JOB_REOD.applyGet(trigger.getJobDataMap());
			
		ParameterParser parser = IDCContextKey.JOB_PARAMETER_PARSER.applyGet(trigger.getJobDataMap());
		
		String contextParameter = IDCContextKey.CONTEXT_PARAMETER.applyGet(trigger.getJobDataMap());
		
		if (isRedo) {
			int instanceId = CONTEXT_INSTANCE_ID.applyGet(trigger.getJobDataMap());
			return updateJobInstance(conn, instanceId, (ins -> {
				ins.setStartTime(LocalDateTime.now());
				ins.setEndTime(null);
				ins.setStatus(JobInstanceStatus.NEW);
				ins.setParameter(parser.parse(ins.getParameter(), contextParameter));
			}));
		} else {
			JobKey jobKey = new JobKey(trigger.getKey().getName(), trigger.getKey().getGroup());
			
			Job job = getIDCDriverDelegate().getIDCJob(conn, jobKey);
			
			JobInstance newIns = new JobInstance();
			// 基本信息
			newIns.setJobId(job.getJobId());
			newIns.setJobGroup(job.getJobGroup());
			newIns.setTaskId(job.getTaskId());
			newIns.setGroupId(job.getGroupId());
			newIns.setTaskName(job.getTaskName());
			newIns.setDescription(job.getDescription());
			newIns.setContentType(job.getContentType());
			newIns.setTaskType(job.getTaskType());
			newIns.setAssignee(job.getAssignee());
			newIns.setParameter(job.getParameter());
			newIns.setScheduleType(job.getScheduleType());
			newIns.setStartTime(LocalDateTime.now());
			newIns.setEndTime(null);
			newIns.setStatus(JobInstanceStatus.NEW);
			newIns.setInstanceType(job.getDispatchType());
			// 参数
			newIns.setParameter(parser.parse(job.getParameter(), contextParameter));
			
			// 其他参数
			if (newIns.getDispatchType() == DispatchType.MANUAL) {
				LocalDateTime loadDate = CONTEXT_LOAD_DATE.applyGet(trigger.getJobDataMap());
				newIns.setLoadDate(loadDate);
				newIns.setNextLoadDate(null);
				newIns.setShouldFireTime(IDCUtils.toEpochMilli(loadDate));
			} else {
				Date shouldFireTime = trigger.getPreviousFireTime();
				LocalDateTime loadDate = toLocalDateTime(shouldFireTime);
				newIns.setLoadDate(loadDate);
				newIns.setNextLoadDate(toLocalDateTime(trigger.getNextFireTime()));
				newIns.setShouldFireTime(shouldFireTime == null ? -1 : shouldFireTime.getTime());
			}
			return getIDCDriverDelegate().insertIDCJobInstance(conn, newIns);
		}
	}
	
	// ACQUIRED --> BLOCKED
	public void triggeredJobComplete(OperableTrigger trigger, JobDetail jobDetail,
			CompletedExecutionInstruction triggerInstCode) {
		/*getContext().updateJobInstance(instance.getInstanceId(), (jobInstance) -> {
			jobInstance.setStatus(JobInstanceStatus.FAILED);
			jobInstance.setEndTime(LocalDateTime.now());
		});
		getContext().updateJobInstance(instance.getInstanceId(), (jobInstance) -> {
			jobInstance.setStatus(JobInstanceStatus.ACCEPTED);
		});*/
	}

	// BLOCKED --> WAITING/ERROR
	public void triggeredAsyncJobComplete(CompleteEvent event) {
        retryExecuteInNonManagedTXLock(
                LOCK_TRIGGER_ACCESS,
                new TransactionCallback<Void>() {
                    public Void execute(Connection conn) throws JobPersistenceException {
                    	JobInstance instance = getIDCDriverDelegate().getIDCJobInstance(conn, event.getInstanceId());
                    	if (instance == null) {
                    		return null;
                    	}
                    	JobKey idcJobKey = instance.getJobKey();
                    	OperableTrigger trigger = retrieveTrigger(new TriggerKey(idcJobKey.getJobId(), idcJobKey.getJobGroup()));
                    	JobDetail jobDetail = retrieveJob(trigger.getJobKey());
                    	CompletedExecutionInstruction instruction = executionCompleteAsync(trigger, event);
                        triggeredJobComplete(conn, trigger, jobDetail, instruction);
                        return null;
                    }
                });    
	}
	
	protected IDCDriverDelegate getIDCDriverDelegate() throws NoSuchDelegateException {
		DriverDelegate delegate = getDelegate();
		if (delegate instanceof IDCDriverDelegate) {
			return (IDCDriverDelegate) delegate;
		}
		throw new NoSuchDelegateException("Not an IDCDriverDelegate");
	}
	
	private CompletedExecutionInstruction executionCompleteAsync(OperableTrigger trigger, CompleteEvent event) {
		if (event.getFinalStatus() == JobInstanceStatus.FINISHED) {
			if (trigger.getNextFireTime() == null) {
				return CompletedExecutionInstruction.NOOP;
			} else {
				return CompletedExecutionInstruction.SET_TRIGGER_COMPLETE;
			}
		} else if(event.getFinalStatus() == JobInstanceStatus.FAILED) {
			return CompletedExecutionInstruction.SET_TRIGGER_ERROR;
		}
		throw new UnsupportedOperationException("Unsupported event status: " + event.getFinalStatus());
	}

	public void updateJobInstance(Integer instanceId, Consumer<JobInstance> func) throws JobPersistenceException {
		executeInLock(null, new TransactionCallback<Void>() {
			@Override
			public Void execute(Connection conn) throws JobPersistenceException {
				updateJobInstance(conn, instanceId, func);
				return null;
			}
		});
	}
	protected JobInstance updateJobInstance(Connection conn, Integer instanceId, Consumer<JobInstance> func) throws JobPersistenceException {

		IDCDriverDelegate delegate = getIDCDriverDelegate();
		JobInstance ins = delegate.getIDCJobInstance(conn, instanceId);
		if (ins == null) {
			return null;
		}
		func.accept(ins);
		getIDCDriverDelegate().updateIDCJobInstance(conn, ins);
		return ins;
	}
	
}
