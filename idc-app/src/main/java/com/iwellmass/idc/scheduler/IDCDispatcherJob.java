package com.iwellmass.idc.scheduler;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;

import javax.inject.Inject;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.executor.IDCJobExecutorService;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.TaskType;

@DisallowConcurrentExecution
public class IDCDispatcherJob implements org.quartz.Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCDispatcherJob.class);

	@Inject
	private IDCJobExecutorServiceFactory executorFactory;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		JobInstance jobInstance = CONTEXT_INSTANCE.applyGet(context);
		
		LOGGER.info("派发任务 {}, 实例 {} ", context.getJobDetail().getKey(), jobInstance.getInstanceId());
		
		// 工作流任务
		if (jobInstance.getTaskType() == TaskType.WORKFLOW) {
			throw new JobExecutionException("not supported yet.");
			// TODO 获取所有子任务
			// 执行它们
		}
		// 独立任务
		else {
			execute(jobInstance);
		}
	}

	// 使用 eureka 来做 HA & balance
	private void execute(JobInstance jobInstance) throws JobExecutionException {
		IDCJobExecutorService executorService = executorFactory.getExecutor(jobInstance);
		try {
			executorService.execute(jobInstance);
		} catch (Throwable e) {
			throw new JobExecutionException("任务派发失败: " + e.getMessage(), false);
		}
	}
}
