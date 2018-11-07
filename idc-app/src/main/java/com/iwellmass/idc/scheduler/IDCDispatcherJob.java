package com.iwellmass.idc.scheduler;

import static com.iwellmass.idc.quartz.IDCContextKey.JOB_INSTANCE;

import javax.inject.Inject;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.iwellmass.idc.executor.IDCJobExecutorService;
import com.iwellmass.idc.model.JobInstance;

@DisallowConcurrentExecution
public class IDCDispatcherJob implements org.quartz.Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCDispatcherJob.class);

	@Inject
	private IDCJobExecutorServiceFactory executorFactory;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		JobInstance jobInstance = JOB_INSTANCE.applyGet(context.getMergedJobDataMap());
		IDCJobExecutorService executorService = executorFactory.getExecutor(jobInstance);
		// 使用 eureka 来做 HA & balance
		try {
			String loadDate = jobInstance.getScheduleType().format(jobInstance.getLoadDate());
			LOGGER.info("派发任务 {}_{}, 实例 ID {}", jobInstance.getJobKey(), loadDate, jobInstance.getInstanceId());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("instance -> {}", JSON.toJSONString(jobInstance));
			}
			executorService.execute(jobInstance);
		} catch (Throwable e) {
			throw new JobExecutionException("任务派发失败: " + e.getMessage(), false);
		}
	}
}
