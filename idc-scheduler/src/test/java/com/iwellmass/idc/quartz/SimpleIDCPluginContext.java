package com.iwellmass.idc.quartz;

import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.ScheduleType;

public class SimpleIDCPluginContext extends IDCPluginContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleIDCPluginContext.class);

	@Override
	public void log(Integer instanceId, String message, Object... args) {
		LOGGER.info(" {} : " + message, instanceId, args);
	}

	@Override
	public void updateJob(JobPK jobKey, Consumer<Job> fun) {
		Job job = new Job();
		job.setTaskId(jobKey.getJobId());
		job.setGroupId(jobKey.getJobGroup());
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
	public JobInstance createJobInstance(JobPK jobKey, Function<Job, JobInstance> fun) {
		Job job = new Job();
		job.setTaskId(jobKey.getJobId());
		job.setGroupId(jobKey.getJobGroup());
		job.setScheduleType(ScheduleType.DAILY);
		JobInstance ins = fun.apply(job);
		ins.setInstanceId(10086);
		return ins;
	}

	@Override
	public void remove(JobPK jobPk) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public JobInstance getJobInstance(Integer instanceId) {
		// TODO Auto-generated method stub
		return null;
	}

}
