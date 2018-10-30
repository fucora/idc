package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCUtils.asJobKey;
import static com.iwellmass.idc.quartz.IDCUtils.parseJobKey;
import static com.iwellmass.idc.quartz.IDCPlugin.getContext;

import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.listeners.SchedulerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.JobKey;

public class IDCSchedulerListener extends SchedulerListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCSchedulerListener.class);


	/* 撤销调度 */
	public void jobUnscheduled(TriggerKey triggerKey) {
		JobKey JobKey = asJobKey(triggerKey);
		getContext().remove(JobKey);
	}

	/* 调度冻结 */
	public void triggerPaused(TriggerKey triggerKey) {
		LOGGER.info("调度任务 {} 已冻结", triggerKey);
	}
	
	/* 调度恢复 */
	public void triggerResumed(TriggerKey triggerKey) {
		LOGGER.info("调度任务 {} 已恢复", triggerKey);
	}
	
	/* 调度完结 */
	public void triggerFinalized(Trigger trigger) {
		JobKey JobKey = parseJobKey(trigger);
		LOGGER.info("调度任务 {} 已完结", JobKey);
	}
	
	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		LOGGER.error("IDCScheduler ERROR: " + msg, cause);
	}
}
