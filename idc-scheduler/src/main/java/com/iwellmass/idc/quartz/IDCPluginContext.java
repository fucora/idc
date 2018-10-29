package com.iwellmass.idc.quartz;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

/**
 * 通过 IDCPlugin，实现 Quartz 与业务交互
 */
public abstract class IDCPluginContext {
	
	// ~~ Job ~~
	public abstract void createJob(Job job);
	
	public abstract void updateJob(JobKey triggerKey, Consumer<Job> fun);
	
	public abstract void remove(JobKey JobKey);
	
	// ~~ JobInstance ~~
	public abstract JobInstance createJobInstance(JobKey jobKey, Function<Job, JobInstance> fun);
	
	public abstract JobInstance getJobInstance(Integer instanceId);
	
	public abstract JobInstance getJobInstance(JobKey jobKey, LocalDateTime loadDate);
	
	public abstract JobInstance updateJobInstance(int instanceId, Consumer<JobInstance> fun);
	
	// ~~ logger ~~
	public abstract void log(Integer instanceId, String message, Object...args);
	
	public abstract void clearLog(Integer instanceId);
	
	public abstract BatchLogger batchLogger(Integer instaceId);
	
	public static class Dependency {}
	
	public interface BatchLogger {
		
		BatchLogger log(String message, Object...args);
		
		public void end();
	}
	
}
