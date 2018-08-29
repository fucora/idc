package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCConstants.CONTEXT_INSTANCE_ID;

import javax.inject.Inject;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.model.ExecutionLog;
import com.iwellmass.idc.repo.ExecutionLogRepository;

@Component
public class IDCJobListener extends JobListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCJobListener.class);

	@Inject
	private ExecutionLogRepository executionLogRepository;

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		String instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		ExecutionLog log = ExecutionLog.createLog(instanceId, "任务被否决");
		executionLogRepository.save(log);
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		String instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		ExecutionLog log = ExecutionLog.createLog(instanceId, "派发任务...");
		executionLogRepository.save(log);
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		if (jobException != null) {
			String instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
			ExecutionLog log = ExecutionLog.createLog(instanceId, "派发任务失败: " + jobException.getMessage());
			executionLogRepository.save(log);
			LOGGER.warn(jobException.getMessage(), jobException);
		} else {
			String instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
			ExecutionLog log = ExecutionLog.createLog(instanceId, "派发任务成功");
			executionLogRepository.save(log);
		}
	}

	@Override
	public String getName() {
		return IDCJobListener.class.getSimpleName();
	}
}
