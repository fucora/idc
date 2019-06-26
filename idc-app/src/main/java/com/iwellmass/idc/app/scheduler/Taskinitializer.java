package com.iwellmass.idc.app.scheduler;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.iwellmass.idc.app.service.JobService;
import com.iwellmass.idc.scheduler.quartz.SuspendScheduleAfterExecution;

import lombok.Setter;

@DisallowConcurrentExecution
@SuspendScheduleAfterExecution
public class Taskinitializer implements org.quartz.Job {
	
	public static final String PROP_SCHEDULE_ID = "scheduleId";

	@Setter
	JobService taskService;
	
	@Setter
	private String scheduleId;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		String batchNo = context.getFireInstanceId();
		
		// 恢复的任务，清理现场
		if (context.isRecovering()) {
			taskService.clear(batchNo);
		}

		try {
			taskService.createTask(batchNo, scheduleId);
		} catch (Exception e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
	}
}