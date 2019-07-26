package com.iwellmass.idc.app.scheduler;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.app.service.JobService;
import com.iwellmass.idc.message.StartMessage;
import com.iwellmass.idc.scheduler.quartz.SuspendScheduleAfterExecution;

import lombok.Setter;

@DisallowConcurrentExecution
@SuspendScheduleAfterExecution
public class JobBootstrap implements org.quartz.Job {
	
	public static final String PROP_TASK_NAME = "taskName";
	
	static final Logger LOGGER = LoggerFactory.getLogger(JobBootstrap.class);

	@Setter
	JobService jobService;
	
	@Setter
	private String taskName;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		try {
			// 全局唯一
			String jobId = context.getFireInstanceId();
			LOGGER.info("开始执行任务：{} , taskName: {} ", jobId, taskName);
			// 恢复的任务，清理现场
			if (context.isRecovering()) {
				jobService.clear(jobId);
			}
			jobService.createJob(jobId, taskName);
			
			StartMessage message = StartMessage.newMessage(jobId);
			message.setMessage("启动任务");
			TaskEventPlugin.eventService(context.getScheduler()).send(message);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}