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
	
	public abstract JobInstance updateJobInstance(int instanceId, Consumer<JobInstance> fun);
	
	public abstract JobInstance getJobInstance(Integer instanceId);
	
	public abstract void remove(JobPK jobPk);
	
	public abstract void log(Integer instanceId, String message, Object...args);
	
	public abstract BatchLogger batchLogger(Integer instaceId);
	
	public abstract void clearLog(Integer instanceId);
	
	public static class Dependency {}
	
	
	public interface BatchLogger {
		
		BatchLogger log(String message, Object...args);
		
		public void end();
	}
	
}
