package com.iwellmass.idc.app.message;

import java.util.Optional;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.message.StartMessage;
import com.iwellmass.idc.message.TaskMessage;
import com.iwellmass.idc.scheduler.model.Job;
import com.iwellmass.idc.scheduler.model.Task;
import com.iwellmass.idc.scheduler.model.TaskType;
import com.iwellmass.idc.scheduler.model.Workflow;
import com.iwellmass.idc.scheduler.quartz.IDCJobStore;
import com.iwellmass.idc.scheduler.repository.JobRepository;

import lombok.Setter;

@DisallowConcurrentExecution
public class TaskEventProcessor implements org.quartz.Job {

	static final Logger LOGGER = LoggerFactory.getLogger(TaskEventProcessor.class);
	
	static final String CXT_JOB_STORE = "idcJobStore";
	static final String CXT_TASK_REPOSITORY = "taskRepository";

	@Setter
	TaskMessage message;

	@Setter
	IDCJobStore idcJobStore;

	@Setter
	JobRepository jobRepository;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		if (message == null) {
			LOGGER.error("ERROR: message cannot be null, trigger {}", context.getTrigger().getKey());
			return;
		}
		Optional<Job> optJob = jobRepository.findById(message.getBatchNo());
		if (!optJob.isPresent()) {
			LOGGER.warn("Cannot process {}, Task {} 不存在", message.getId(), message.getBatchNo());
			return;
		}
		Job job = optJob.get();

		try {
			switch (message.getEvent()) {
			case START: {
				job.start();
				break;
			}
			case RENEW: {
				job.renew();
				break;
			}
			case FINISH: {
				job.finish();
				// TODO
				// lockSupport.unlockTrigger(Schedule.buildTriggerKey(task.getScheduleId()),
				// UnlockInstruction.SET_UNLOCK);
				break;
			}
			case FAIL: {
				job.fail();
				// TODO
				// lockSupport.unlockTrigger(Schedule.buildTriggerKey(task.getScheduleId()),
				// UnlockInstruction.SET_ERROR);
				break;
			}
			default: {
				// bad message...
				LOGGER.error("Cannot process {}, unsupported event {}", message.getId(), message.getEvent());
			}
			}
		} catch (Exception e) {
			LOGGER.error("Cannot process {}, {}", message.getId(), e.getMessage());
		}
		
		
		// 工作流任务，检查是否触发下个任务
		if (job.getTaskType() == TaskType.WORKFLOW) {
			
			Workflow workflow = null;
			
			StartMessage message = StartMessage.newMessage("gogo");
			
			Task nextTask = null;
			
			TaskEventPlugin.eventService(context.getScheduler()).send(message);;
		}
		
	}
}
