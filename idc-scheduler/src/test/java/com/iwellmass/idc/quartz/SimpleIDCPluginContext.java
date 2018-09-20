package com.iwellmass.idc.quartz;

import java.util.function.Consumer;
import java.util.function.Function;

import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;

public class SimpleIDCPluginContext extends IDCPluginContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleIDCPluginContext.class);

	@Override
	public void log(Integer instanceId, String message, Object... args) {
		LOGGER.info(" {} : " + message, instanceId, args);
	}

	@Override
	public void updateJob(JobKey jobKey, Consumer<Job> fun) {
		Job job = new Job();
		job.setTaskId(jobKey.getName());
		job.setGroupId(jobKey.getGroup());
		fun.andThen(v -> LOGGER.info("更新任务 : {}", v)).accept(job);
	}

	@Override
	public void updateJobInstance(int instanceId, Consumer<JobInstance> fun) {
		JobInstance jobInstance = new JobInstance();
		jobInstance.setInstanceId(instanceId);
		jobInstance.setTaskId("test");
		jobInstance.setGroupId("test");
		fun.andThen(i -> LOGGER.info("更新调度 : {}", i)).accept(jobInstance);
	}

	@Override
	public JobInstance createJobInstance(JobKey jobKey, Function<Job, JobInstance> fun) {
		Job job = new Job();
		job.setTaskId(jobKey.getName());
		job.setGroupId(jobKey.getGroup());
		return fun.apply(job);
	}
}
