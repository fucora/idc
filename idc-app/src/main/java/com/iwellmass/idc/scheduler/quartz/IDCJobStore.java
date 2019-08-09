package com.iwellmass.idc.scheduler.quartz;

import org.quartz.JobDetail;
import org.quartz.JobPersistenceException;
import org.quartz.TriggerKey;
import org.quartz.spi.JobStore;
import org.quartz.utils.ClassUtils;

public interface IDCJobStore extends JobStore {
	
	String STATE_SUSPENDED = "SUSPENDED";
	String STATE_PAUSED_SUSPENDED = "PAUSED_SUSPENDED";

	void releaseTrigger(TriggerKey triggerKey, ReleaseInstruction inst);
	
	default boolean isSuspendAfterExecution(JobDetail jobDetail) {
		return ClassUtils.isAnnotationPresent(jobDetail.getJobClass(), SuspendScheduleAfterExecution.class);
	}
}
