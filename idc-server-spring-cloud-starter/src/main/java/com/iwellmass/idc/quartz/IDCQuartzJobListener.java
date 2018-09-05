package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCConstants.CONTEXT_INSTANCE_ID;

import javax.inject.Inject;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.repo.ExecutionLogRepository;

@Component
public class IDCQuartzJobListener extends JobListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCQuartzJobListener.class);

	@Inject
	private ExecutionLogRepository executionLogRepository;

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		Integer instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		executionLogRepository.log(instanceId, "跳过执行");
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		Integer instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		executionLogRepository.log(instanceId, "派发任务...");
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		Integer instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		if (jobException != null) {
			executionLogRepository.log(instanceId, "派发任务失败: " + jobException.getMessage());
			LOGGER.warn(jobException.getMessage(), jobException);
		} else {
			executionLogRepository.log(instanceId, "派发任务成功");
		}
	}

	@Override
	public String getName() {
		return IDCQuartzJobListener.class.getSimpleName();
	}
}
