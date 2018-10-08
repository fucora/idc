package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_DISPATCH_TYPE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_REOD;

import java.time.LocalDateTime;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.ScheduleStatus;

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
		
		JobPK jobPK = IDCPlugin.toJobPK(context.getTrigger());
		
		if (jobException != null) {
			pluginContext.log(instance.getInstanceId(), "派发任务失败: " + jobException.getMessage());
			pluginContext.updateJobInstance(instance.getInstanceId(), (jobInstance) -> {
				jobInstance.setStatus(JobInstanceStatus.FAILED);
				jobInstance.setEndTime(LocalDateTime.now());
			});
			pluginContext.updateJob(jobPK, (job)->{
				job.setUpdateTime(LocalDateTime.now());
				job.setStatus(ScheduleStatus.ERROR);
			});
		} else {
			pluginContext.log(instance.getInstanceId(), "等待执行结果...");
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