package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCPlugin.getContext;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

import com.iwellmass.idc.model.JobInstance;

public class IDCJobListener extends JobListenerSupport {

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		JobInstance jobInstance = CONTEXT_INSTANCE.applyGet(context.getMergedJobDataMap());
		getContext().log(jobInstance.getInstanceId(), "派发任务，业务日期 {}", 
				jobInstance.getScheduleType().format(jobInstance.getLoadDate()));
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		JobInstance instance = CONTEXT_INSTANCE.applyGet(context.getMergedJobDataMap());
		
		if (jobException != null) {
			getContext().log(instance.getInstanceId(), "派发任务失败: " + jobException.getMessage());
		} else {
			getContext().log(instance.getInstanceId(), "等待执行结果...");
		}
	}
	
	@Override
	public String getName() {
		return IDCJobListener.class.getSimpleName();
	}
}