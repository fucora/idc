package com.iwellmass.idc.scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.model.ExecutionLog;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.quartz.IDCPluginContext;
import com.iwellmass.idc.repo.ExecutionLogRepository;
import com.iwellmass.idc.repo.JobDependencyRepository;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.repo.JobRepository;

@Component
public class StdIDCPluginContext extends IDCPluginContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(StdIDCPluginContext.class);

	@Inject
	private JobRepository jobRepo;

	@Inject
	private JobInstanceRepository instanceRepo;

	@Inject
	private ExecutionLogRepository logRepository;
	
	@Inject
	private JobDependencyRepository dependencyRepo;
	
	@Override
	public JobInstance createJobInstance(JobPK jobKey, Function<Job, JobInstance> fun) {
		Job job = jobRepo.findOne(jobKey);
		
		Assert.isTrue(job != null, "任务 %s 不存在", jobKey);
		
		JobInstance instance = fun.apply(job);
		
		JobInstance check = instanceRepo.findOne(instance.getTaskId(), instance.getGroupId(), instance.getLoadDate());
		if (check != null) {
			instance.setInstanceId(check.getInstanceId());
		}
		return instanceRepo.save(instance);
	}

	@Override
	public void updateJob(JobPK jobKey, Consumer<Job> fun) {
		Job job = jobRepo.findOne(jobKey);
		fun.accept(job);
		jobRepo.save(job);
	}

	@Override
	public JobInstance updateJobInstance(int instanceId, Consumer<JobInstance> fun) {
		JobInstance instance = instanceRepo.findOne(instanceId);
		fun.accept(instance);
		return instanceRepo.save(instance);
	}

	@Override
	public void log(Integer instanceId, String message, Object... args) {
		logRepository.log(instanceId, message, args);
	}

	@Override
	public void remove(JobPK jobPk) {

		// 清空日志信息
		LOGGER.info("删除日志信息");
		Utils.safeExecute(()-> {
			logRepository.deleteByJob(jobPk);
			return null;
		});
		
		// 清空实例信息
		LOGGER.info("删除实例信息");
		Utils.safeExecute(()-> {
			instanceRepo.deleteByJob(jobPk);
			return null;
		});
		
		// 删除依赖信息
		LOGGER.info("删除依赖信息");
		Utils.safeExecute(()-> {
			dependencyRepo.deleteByJob(jobPk);
			return null;
		});
		
		// 删除调度任务
		LOGGER.info("删除调度任务");
		Utils.safeExecute(()-> {
			jobRepo.delete(jobPk);
			return null;
		});
	}

	@Override
	public JobInstance getJobInstance(Integer instanceId) {
		return instanceRepo.findOne(instanceId);
	}

	@Override
	public void clearLog(Integer instanceId) {
		logRepository.deleteByInstanceId(instanceId);
	}
	
	@Override
	public BatchLogger batchLogger(Integer instanceId) {
		return new BatchLoggerImpl(instanceId);
	}
	
	class BatchLoggerImpl implements BatchLogger {
		
		Integer instanceId = null;
		private List<ExecutionLog> logs;
		
		public BatchLoggerImpl(Integer instanceId) {
			this.instanceId = instanceId;
		}

		@Override
		public BatchLogger log(String message, Object... args) {
			if (message == null || message.isEmpty()) {
				return this;
			}
			if (logs == null) {
				logs = new LinkedList<>();
			}
			logs.add(ExecutionLog.createLog(instanceId, message, args));
			return this;
		}

		@Override
		public void end() {
			logRepository.save(logs);
			logs = null;
		}
	}

}