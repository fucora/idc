package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.*;

import java.text.SimpleDateFormat;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.JobInstanceStatus;


@DisallowConcurrentExecution
public class SimpleJob implements Job{

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleJob.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		LOGGER.info("execute job: {}", sdf.format(context.getScheduledFireTime()));
		
		
		
		Thread thread = new Thread(()->{
			try {
				IDCPlugin plugin = IDC_PLUGIN.applyGet(context.getScheduler().getContext());
				TriggerKey key = context.getTrigger().getKey();
				plugin.completeAsyncJob(key.getName(), key.getGroup(), JobInstanceStatus.FINISHED);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		});
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LOGGER.info("simple job executed.");
		
	}

}
