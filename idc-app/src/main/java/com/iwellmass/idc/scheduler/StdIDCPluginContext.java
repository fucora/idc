package com.iwellmass.idc.scheduler;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;

import org.quartz.JobKey;
import org.springframework.stereotype.Component;

import com.iwellmass.common.util.Assert;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.quartz.IDCPluginContext;
import com.iwellmass.idc.repo.ExecutionLogRepository;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.repo.JobRepository;

@Component
public class StdIDCPluginContext extends IDCPluginContext {

	@Inject
	private JobRepository jobRepo;

	@Inject
	private JobInstanceRepository instanceRepo;

	@Inject
	private ExecutionLogRepository logRepository;
	
	@Override
	public JobInstance createJobInstance(JobPK jobKey, Function<Job, JobInstance> fun) {
		Job job = jobRepo.findOne(jobKey.getJobId(), jobKey.getJobGroup());
		
		Assert.isTrue(job != null, "任务不存在");
		
		JobInstance instance = fun.apply(job);
		
		JobInstance check = instanceRepo.findOne(instance.getTaskId(), instance.getGroupId(), instance.getLoadDate());
		if (check != null) {
			instance.setInstanceId(check.getInstanceId());
		}
		return instanceRepo.save(instance);
	}

	@Override
	public void updateJob(JobPK jobKey, Consumer<Job> fun) {
		Job job = jobRepo.findOne(jobKey.getJobId(), jobKey.getJobGroup());
		fun.accept(job);
		jobRepo.save(job);
	}

	@Override
	public void updateJobInstance(int instanceId, Consumer<JobInstance> fun) {
		JobInstance instance = instanceRepo.findOne(instanceId);
		fun.accept(instance);
		instanceRepo.save(instance);
	}

	@Override
	public void log(Integer instanceId, String message, Object... args) {
		logRepository.log(instanceId, message, args);
	}
}