package com.iwellmass.idc.quartz;

import java.util.function.Consumer;
import java.util.function.Function;

import org.quartz.JobKey;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;

/**
 * 通过 IDCPlugin，实现 Quartz 与业务交互
 */
public abstract class IDCPluginContext {
	
	public abstract JobInstance createJobInstance(JobKey jobKey, Function<Job, JobInstance> fun);
	
	public abstract void updateJob(JobKey jobKey, Consumer<Job> fun);
	
	public abstract void updateJobInstance(int instanceId, Consumer<JobInstance> fun);
	
	public abstract void log(Integer instanceId, String message, Object...args);
	
	public static class Dependency {}
	
}
