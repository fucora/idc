package com.iwellmass.idc.quartz;

import java.util.function.Consumer;
import java.util.function.Function;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobPK;

/**
 * 通过 IDCPlugin，实现 Quartz 与业务交互
 */
public abstract class IDCPluginContext {
	
	public abstract void updateJob(JobPK triggerKey, Consumer<Job> fun);
	
	public abstract JobInstance createJobInstance(JobPK jobKey, Function<Job, JobInstance> fun);
	
	public abstract void updateJobInstance(int instanceId, Consumer<JobInstance> fun);
	
	public abstract void log(Integer instanceId, String message, Object...args);
	
	public static class Dependency {}
	
}
