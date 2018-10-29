package com.iwellmass.idc.quartz;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

public class SimpleIDCPluginContext extends IDCPluginContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleIDCPluginContext.class);
	
	private static final Map<JobKey, Job> jobMap = new HashMap<>();
	private static final Map<Integer, JobInstance> jobInstanceMap = new HashMap<>();

	@Override
	public void log(Integer instanceId, String message, Object... args) {
		LOGGER.info("[" + instanceId + "] >> " + message, args);
	}
	
	@Override
	public void createJob(Job job) {
		jobMap.put(job.getJobKey(), job);
	}

	@Override
	public void updateJob(JobKey jobKey, Consumer<Job> fun) {
		Job job = jobMap.get(jobKey);
		fun.andThen(v -> LOGGER.info("更新任务 : {}", v)).accept(job);
	}

	@Override
	public JobInstance updateJobInstance(int instanceId, Consumer<JobInstance> fun) {
		JobInstance jobInstance = new JobInstance();
		jobInstance.setInstanceId(instanceId);
		jobInstance.setTaskId("test");
		jobInstance.setGroupId("test");
		fun.andThen(i -> LOGGER.info("更新调度 : {}", i)).accept(jobInstance);
		return jobInstance;
	}

	@Override
	public JobInstance createJobInstance(JobKey jobKey, Function<Job, JobInstance> fun) {
		synchronized (jobInstanceMap) {
			Job job = jobMap.get(jobKey);
			JobInstance ins = fun.apply(job);
			ins.setInstanceId(jobInstanceMap.size() + 1);
			jobInstanceMap.put(ins.getInstanceId(), ins);
			return ins;
		}
	}

	@Override
	public void remove(JobKey jobKey) {
		
	}


	@Override
	public JobInstance getJobInstance(Integer instanceId) {
		return jobInstanceMap.get(instanceId);
	}

	@Override
	public JobInstance getJobInstance(JobKey jobKey, LocalDateTime loadDate) {
		for (JobInstance entry : jobInstanceMap.values()) {
			if (entry.getJobKey().equals(jobKey) && entry.getLoadDate().isEqual(loadDate)) {
				return entry;
			}
		}
		throw new NullPointerException("实例不存在");
	}
	
	@Override
	public void clearLog(Integer instanceId) {}

	@Override
	public BatchLogger batchLogger(Integer instaceId) {
		return new BatchLogger() {
			@Override
			public BatchLogger log(String message, Object... args) {
				SimpleIDCPluginContext.this.log(instaceId, message, args);
				return this;
			}
			
			@Override
			public void end() {}
		};
	}
	
}
