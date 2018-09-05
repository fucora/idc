package com.iwellmass.idc.quartz;

import java.util.List;

import javax.inject.Inject;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.IDCJobExecutorServiceFactory;
import com.iwellmass.idc.executor.IDCJobExecutorService;
import com.iwellmass.idc.model.ContentType;
import com.iwellmass.idc.model.ExecutionLog;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.repo.JobRepository;

@DisallowConcurrentExecution
public class IDCDispatcherJob implements org.quartz.Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCDispatcherJob.class);
	
	@Inject
	private JobRepository jobRepository;
	
	@Inject
	private JobInstanceRepository instanceRepository;

	@Inject
	private IDCJobExecutorServiceFactory executorFactory;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		LOGGER.info("派发任务 {} ", context.getJobDetail().getKey());
		Integer id = IDCPlugin.CONTEXT_INSTANCE_ID.applyGet(context);
		JobInstance jobInstance = instanceRepository.findOne(id);
		
		Job job = jobRepository.findOne(jobInstance.getTaskId(), jobInstance.getGroupId());
		
		// 工作流任务
		if (jobInstance.getTaskType() == TaskType.WORKFLOW) {
			// TODO 获取所有子任务
			// 执行它们
			executeSubJobs(jobInstance);
		}
		// 独立任务
		else {
			execute(job, jobInstance);
		}
	}

	// 执行子任务
	private void executeSubJobs(JobInstance instance) {

		// TODO 获取可用的子任务

		List<Job> subJobs = null;

		if (Utils.isNullOrEmpty(subJobs)) {
			// TODO 通知已经执行完毕
			ExecutionLog log = new ExecutionLog();
			log.setMessage("执行完毕");
		} else {

		}
	}

	// 使用 eureka 来做 HA & balance
	private void execute(Job job, JobInstance jobInstance) throws JobExecutionException {
		
		String jobName = getExecutorName(job.getContentType());
		
		IDCJobExecutorService executorService = executorFactory.getExecutor(jobInstance.getDomain(), jobName);
		
		try {
			executorService.execute(jobInstance);
		} catch (Throwable e) {
			throw new JobExecutionException("任务派发失败: " + e.getMessage(), false);
		}
	}
	
	private static final String getExecutorName(ContentType contentType) {
		return contentType.toString().toLowerCase();
	}
	
	
}
