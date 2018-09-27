package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE_ID;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_SCHEDULE_TYPE;

import java.time.LocalDateTime;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.ScheduleType;

public class IDCJobListener extends JobListenerSupport {

	private final IDCPluginContext pluginContext;
	
	public IDCJobListener(IDCPluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		Integer instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		LocalDateTime loadDate = CONTEXT_LOAD_DATE.applyGet(context);
		ScheduleType scheduleType = JOB_SCHEDULE_TYPE.applyGet(context.getJobDetail().getJobDataMap());
		pluginContext.log(instanceId, "派发任务，业务日期 {}", scheduleType.format(loadDate));
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		Integer instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		if (jobException != null) {
			pluginContext.log(instanceId, "派发任务失败: " + jobException.getMessage());
			pluginContext.updateJobInstance(instanceId, (jobInstance) -> {
				jobInstance.setStatus(JobInstanceStatus.FAILED);
				jobInstance.setEndTime(LocalDateTime.now());
			});
		} else {
			pluginContext.updateJobInstance(instanceId, (jobInstance) -> {
				jobInstance.setStatus(JobInstanceStatus.ACCEPTED);
			});
		}
	}

	@Override
	public String getName() {
		return IDCJobListener.class.getSimpleName();
	}
}