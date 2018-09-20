package com.iwellmass.idc.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.repo.ExecutionLogRepository;
import com.iwellmass.idc.repo.JobInstanceRepository;

@Service
public class StdIDCStatusService implements IDCStatusService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StdIDCStatusService.class);

	@Inject
	private JobInstanceRepository jobInstanceRepository;

	@Inject
	private ExecutionLogRepository jobLogger;
	
	@Override
	public void fireStartEvent(StartEvent event) {
		// 更新任务状态
		JobInstance jobInstance = jobInstanceRepository.findOne(event.getInstanceId());
		jobInstance.setStartTime(event.getStartTime());
		jobInstance.setStatus(JobInstanceStatus.RUNNING);
		jobLogger.log(event);
	}

	@Override
	public void fireCompleteEvent(CompleteEvent event) {
		
		LOGGER.info("任务执行完毕: {}", event);
		
		// 更新实例状态
		JobInstance jobInstance = jobInstanceRepository.findOne(event.getInstanceId());
		
		if (jobInstance == null) {
			LOGGER.warn("无法更新 {} 运行状态, 不存在此实例", event.getInstanceId());
			return;
		}
		
		jobInstance.setEndTime(event.getEndTime());
		jobInstance.setStatus(event.getFinalStatus());
		jobInstanceRepository.save(jobInstance);

		
		// 任务执行成功
		if (event.getFinalStatus() == JobInstanceStatus.FINISHED) {
			
//			if (nextLoadDate == null) {
//				LOGGER.info("Job {}.{} finalized.", jobInstance.getGroupId(), jobInstance.getTaskId());
//			}
		}
		
		// 记录日志
		jobLogger.log(event);
	}
}
