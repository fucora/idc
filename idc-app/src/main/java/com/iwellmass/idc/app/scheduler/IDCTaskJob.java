package com.iwellmass.idc.app.scheduler;

import javax.inject.Inject;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IDCTaskJob implements org.quartz.Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCTaskJob.class);

	@Inject
	private IDCTaskFactory executorFactory;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

//		JobInstance jobInstance = null;//CONTEXT_INSTANCE.applyGet(context);
//		IDCJobExecutorService executorService = executorFactory.getExecutor(jobInstance);
//		// 使用 eureka 来做 HA & balance
//		try {
//			String loadDate = jobInstance.getLoadDate();
//			LOGGER.info("派发任务 {}_{}, 实例 ID {}", jobInstance.getJobId(), loadDate, jobInstance.getInstanceId());
//			if (LOGGER.isDebugEnabled()) {
//				// LOGGER.debug("instance -> {}", JSON.toJSONString(jobInstance));
//			}
//			executorService.execute(jobInstance);
//		} catch (Throwable e) {
//			throw new JobExecutionException("任务派发失败: " + e.getMessage(), false);
//		}
	}
}
