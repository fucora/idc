package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCConstants.CONTEXT_INSTANCE_ID;

import javax.inject.Inject;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.repo.ExecutionLogRepository;
import com.iwellmass.idc.repo.JobInstanceRepository;

@Component
public class IDCQuartzJobListener extends JobListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCQuartzJobListener.class);

	@Inject
	private JobInstanceRepository jobInstanceRepository;
	
	@Inject
	private ExecutionLogRepository logRepository;

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		Integer instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		logRepository.log(instanceId, "跳过执行");
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		Integer instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		logRepository.log(instanceId, "派发任务...");
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		Integer instanceId = CONTEXT_INSTANCE_ID.applyGet(context);
		if (jobException != null) {
			logRepository.log(instanceId, "派发任务失败: " + jobException.getMessage());
			LOGGER.error(jobException.getMessage(), jobException);
			
			JobInstance jobInstance = jobInstanceRepository.findOne(instanceId);
			jobInstance.setStatus(JobInstanceStatus.FAILED);
			jobInstanceRepository.save(jobInstance);
			
		} else {
			logRepository.log(instanceId, "派发任务成功");
		}
	}

	@Override
	public String getName() {
		return IDCQuartzJobListener.class.getSimpleName();
	}
}
