package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;

import java.time.LocalDateTime;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;

public class IDCJobListener extends JobListenerSupport {

	private final IDCPluginContext pluginContext;

	public IDCJobListener(IDCPluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		JobInstance jobInstance = CONTEXT_INSTANCE.applyGet(context.getMergedJobDataMap());
		pluginContext.log(jobInstance.getInstanceId(), "派发任务，业务日期 {}", 
				jobInstance.getScheduleType().format(jobInstance.getLoadDate()));
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		JobInstance instance = CONTEXT_INSTANCE.applyGet(context.getMergedJobDataMap());

		if (jobException != null) {
			pluginContext.log(instance.getInstanceId(), "派发任务失败: " + jobException.getMessage());
			pluginContext.updateJobInstance(instance.getInstanceId(), (jobInstance) -> {
				jobInstance.setStatus(JobInstanceStatus.FAILED);
				jobInstance.setEndTime(LocalDateTime.now());
			});
		} else {
			pluginContext.updateJobInstance(instance.getInstanceId(), (jobInstance) -> {
				jobInstance.setStatus(JobInstanceStatus.ACCEPTED);
			});
		}
	}

	@Override
	public String getName() {
		return IDCJobListener.class.getSimpleName();
	}
}