package com.iwellmass.idc.app.message;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.repository.WorkflowRepository;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.message.JobMessage;
import com.iwellmass.idc.scheduler.quartz.IDCJobStore;
import com.iwellmass.idc.scheduler.quartz.ReleaseInstruction;
import com.iwellmass.idc.scheduler.repository.AllJobRepository;

import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

@DisallowConcurrentExecution
public class TaskEventProcessor implements org.quartz.Job {

	static final Logger LOGGER = LoggerFactory.getLogger(TaskEventProcessor.class);
	
	static final String CXT_JOB_SERVICE = "jobService";
	static final String CXT_JOB_STORE = "idcJobStore";
	static final String CXT_ALL_JOB_REPOSITORY = "allJobRepository";
	static final String CXT_ALL_WORKFLOW_REPOSITORY = "workflowRepository";

	@Setter
	JobMessage message;

	@Setter
	IDCJobStore idcJobStore;

	@Setter
	AllJobRepository allJobRepository;

	@Setter
	WorkflowRepository workflowRepository;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// safe execute...
		try {
			doExecute(context);
		} catch (Exception e) {
			LOGGER.error("ERROR: " + message );
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void doExecute(JobExecutionContext context) {

		if (message == null) {
			LOGGER.error("ERROR: message cannot be null, trigger {}", context.getTrigger().getKey());
			return;
		}
		Optional<AbstractJob> opt = allJobRepository.findById(message.getJobId());
		
		if (!opt.isPresent()) {
			LOGGER.warn("Cannot process {}, Task {} 不存在", message.getId(), message.getJobId());
			return;
		}
		
		AbstractJob runningJob = opt.get();
		//提前填充job
		AbstractTask abstractTask =  runningJob.getTask();
		if(abstractTask.getTaskType()== TaskType.WORKFLOW)
		{
		  Workflow workflow = workflowRepository.findById(abstractTask.getTaskId()).get();
		  abstractTask.setWorkflow(workflow);
		}
		try {
			switch (message.getEvent()) {
			case START: {
				runningJob.start();
				break;
			}
			case RENEW: {
				runningJob.renew();
				break;
			}
			case FINISH: {
				runningJob.complete(JobState.FINISHED);
				break;
			}
			case FAIL: {
				runningJob.complete(JobState.FAILED);
				break;
			}
			default: {
				// bad message...
				LOGGER.error("Cannot process {}, unsupported event {}", message.getId(), message.getEvent());
			}
			}
		} catch (Exception e) {
			LOGGER.error("Cannot process {}, {}", message.getId(), e.getMessage());
			LOGGER.error(e.getMessage(), e);
		}
		
		// Release trigger
		if (runningJob instanceof Job) {
			Job job = (Job) runningJob;
			TriggerKey tk = job.getTask().getTriggerKey();
			if (job.getState().isSuccess()) {
				idcJobStore.releaseTrigger(tk, ReleaseInstruction.RELEASE);
			} else {
				idcJobStore.releaseTrigger(tk, ReleaseInstruction.SET_ERROR);
			}
		} 
		else {
			// 刷新主任务
		}
	}
}
