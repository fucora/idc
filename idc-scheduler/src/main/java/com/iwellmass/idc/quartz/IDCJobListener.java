package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE_ID;

import java.time.LocalDateTime;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.JobInstanceStatus;

public class IDCJobListener extends JobListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCJobListener.class);

	private final IDCPluginContext pluginContext;
	
	public IDCJobListener(IDCPluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		Integer instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		
		Date loadDate = context.getScheduledFireTime();
		pluginContext.log(instanceId, "派发 {} 任务", loadDate.getTime());
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		Integer instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		if (jobException != null) {
			pluginContext.log(instanceId, "派发任务失败: " + jobException.getMessage());
			LOGGER.error(jobException.getMessage(), jobException);
			
			pluginContext.updateJobInstance(instanceId, (jobInstance) -> {
				jobInstance.setStatus(JobInstanceStatus.FAILED);
				jobInstance.setEndTime(LocalDateTime.now());
			});
		}
	}

	@Override
	public String getName() {
		return IDCJobListener.class.getSimpleName();
	}
}