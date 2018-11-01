package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.JOB_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;

public class IDCJobListener extends JobListenerSupport {

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
	
		JobInstance instance = JOB_INSTANCE.applyGet(context.getMergedJobDataMap());
		IDCPlugin idcPlugin = IDC_PLUGIN.applyGet(context.getScheduler());
		idcPlugin.fireProgressEvent(ProgressEvent.newEvent(instance.getInstanceId())
			.setStatus(JobInstanceStatus.NEW)
			.setMessage("派发任务，业务日期 {}", instance.getScheduleType().format(instance.getLoadDate())));
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		
		JobInstance instance = JOB_INSTANCE.applyGet(context.getMergedJobDataMap());
		IDCPlugin idcPlugin = IDC_PLUGIN.applyGet(context.getScheduler());
		if (jobException != null) {
			// 通知任务已经完成
			idcPlugin.fireCompleteEvent(CompleteEvent.failureEvent()
				.setInstanceId(instance.getInstanceId())
				.setMessage("派发任务失败: {}", jobException.getMessage()));
		} else {
			// 通知任务进行中
			idcPlugin.fireProgressEvent(ProgressEvent.newEvent(instance.getInstanceId())
				.setStatus(JobInstanceStatus.ACCEPTED)	
				.setMessage("等待执行结果..."));
		}
	}
	
	@Override
	public String getName() {
		return IDCJobListener.class.getSimpleName();
	}
}