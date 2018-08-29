package com.iwellmass.idc.quartz;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.dag.DAGSchedulingEngine;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.ExecutionLog;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.service.ExecutionRequest;
import com.iwellmass.idc.service.JobExecutorService;

@DisallowConcurrentExecution
public class IDCJob implements org.quartz.Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCJob.class);

	private JobInstance jobInstance;
	
	private JobExecutorService executorService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		LOGGER.info("执行 {} ", context.getJobDetail().getKey());

		// 工作流任务
		if (jobInstance.getTaskType() == TaskType.WORKFLOW) {
			// TODO 获取所有子任务
			// 执行它们
			executeSubJobs(jobInstance);
		}
		// 独立任务
		else {
			execute(jobInstance);
		}
	}

	// 执行子任务
	private void executeSubJobs(JobInstance instance) {

		DAGSchedulingEngine dagEngine = null;

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
	private void execute(JobInstance jobInstance) throws JobExecutionException {
		ExecutionRequest request = new ExecutionRequest();
		request.setInstanceId(jobInstance.getId());
		request.setTaskId(jobInstance.getTaskId());
		request.setGroup(jobInstance.getGroupId());
		request.setParameters(jobInstance.getParameters());
		try {
			executorService.execute(request);
		} catch (Throwable e) {
			throw new JobExecutionException("执行任务失败: " + e.getMessage(), false);
		}
	}
}
