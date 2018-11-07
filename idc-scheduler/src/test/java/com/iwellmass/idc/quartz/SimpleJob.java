package com.iwellmass.idc.quartz;

import java.text.SimpleDateFormat;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class SimpleJob implements Job{

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		LOGGER.info("execute job: {}", sdf.format(context.getScheduledFireTime()));
		
		String instanceId = context.getFireInstanceId();
		
		
		// throw new JobExecutionException("执行失败");
	}
}
