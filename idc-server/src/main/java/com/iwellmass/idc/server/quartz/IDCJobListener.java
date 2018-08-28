package com.iwellmass.idc.server.quartz;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IDCJobListener extends JobListenerSupport{

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCJobListener.class);
	
	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		JobKey jobKey = context.getJobDetail().getKey();
		LocalDateTime ldt = IDCPlugin.toLocalDateTime(context.getScheduledFireTime());
		LOGGER.info("否决任务 {}.{}.{}", jobKey.getGroup(), jobKey.getName(), ldt.format(DateTimeFormatter.BASIC_ISO_DATE));
	}
	
	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		
		Object result = context.getResult();
		
		if (context.isRecovering()) {
			// 恢复终端的任务，我们认为任务并没有执行
		} else {
			// 异步任务
			try {
				TriggerKey tk = context.getTrigger().getKey();
				LOGGER.debug("暂停 {}", tk);
				context.getScheduler().pauseTrigger(tk);
			} catch (SchedulerException e) {
				LOGGER.error("", e);
			}
		}
	}
	
	@Override
	public String getName() {
		return IDCJobListener.class.getSimpleName();
	}
}
