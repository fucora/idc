package com.iwellmass.idc.quartz;

import java.text.SimpleDateFormat;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.iwellmass.idc.model.JobInstance;

@DisallowConcurrentExecution
public class SimpleJob implements Job{

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		LOGGER.info("execute job: {}", sdf.format(context.getScheduledFireTime()));

		JobInstance jobIns = IDCContextKey.CONTEXT_INSTANCE.applyGet(context);
		
		System.out.println(JSON.toJSONString(jobIns));
		throw new JobExecutionException("failure");
	}
}
