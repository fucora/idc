package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_DISPATCH_TYPE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_ASYNC;
import static com.iwellmass.idc.quartz.IDCPlugin.toLocalDateTime;

import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.listeners.SchedulerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.ScheduleStatus;
import com.iwellmass.idc.model.ScheduleType;

public class IDCSchedulerListener extends SchedulerListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCSchedulerListener.class);

	private final IDCPluginContext pluginContext;
	
	public IDCSchedulerListener(IDCPluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}

	@Override
	public void jobAdded(JobDetail jobDetail) {
		JobKey jobKey = jobDetail.getKey();
		Boolean isAsync = JOB_ASYNC.applyGet(jobDetail.getJobDataMap());
		LOGGER.info("任务 {} 已添加, 调度方式 {}", jobKey, isAsync ? "async" : "sync");
	}
	
	@Override
	public void jobScheduled(Trigger trigger) {
		
		JobKey jobKey = trigger.getJobKey();
		
		Boolean isRedo = false;
		
		DispatchType scheduleType = CONTEXT_DISPATCH_TYPE.applyGet(trigger.getJobDataMap());
		
		if (isRedo) {
			LOGGER.info("重跑任务 {}", jobKey);
		} else {
			pluginContext.updateJob(jobKey, (job) -> {
				// 更新调度信息
				if (scheduleType == DispatchType.MANUAL) {
					job.setStatus(ScheduleStatus.NORMAL);
					job.setNextLoadDate(null);
					job.setPrevLoadDate(CONTEXT_LOAD_DATE.applyGet(trigger.getJobDataMap()));
				} else {
					Date nextFireTime = trigger.getNextFireTime();
					// next fire time
					job.setNextLoadDate(toLocalDateTime(nextFireTime));
					// prev fire time
					job.setPrevLoadDate(toLocalDateTime(trigger.getPreviousFireTime()));
				}
			});
		}
	}
	
	@Override
	public void triggerFinalized(Trigger trigger) {
		
		JobKey jobKey = trigger.getJobKey();
		LOGGER.info("任务 {} 完结", trigger.getJobKey());
		
		pluginContext.updateJob(jobKey, (job) -> {
			job.setStatus(ScheduleStatus.COMPLETE);
		});
	}
	
	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		LOGGER.error("调度错误: " + msg, cause);
	}
}
