package com.iwellmass.idc.service;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.Sentinel;
import com.iwellmass.idc.model.SentinelPK;
import com.iwellmass.idc.model.SentinelStatus;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.repo.ExecutionLogRepository;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.repo.SentinelRepository;

@Service
public class StdIDCStatusService implements IDCStatusService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StdIDCStatusService.class);

	@Inject
	private JobInstanceRepository jobInstanceRepository;

	@Inject
	private ExecutionLogRepository jobLogRepository;
	
	@Inject
	SentinelRepository sentinelRepository;


	@Override
	public void fireStartEvent(StartEvent event) {
		// 更新任务状态
		JobInstance jobInstance = jobInstanceRepository.findOne(event.getInstanceId());
		jobInstance.setStartTime(event.getStartTime());
		jobInstance.setStatus(JobInstanceStatus.RUNNING);
		jobLogRepository.log(event);
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
		
		// 记录日志
		jobLogRepository.log(event);
		
		
		LocalDateTime nextLoadDate = jobInstance.getNextLoadDate();
		
		// 任务执行成功
		if (event.getFinalStatus() == JobInstanceStatus.FINISHED) {
			
			if (nextLoadDate == null) {
				LOGGER.info("Job {}.{} finalized.", jobInstance.getGroupId(), jobInstance.getTaskId());
			} else {
				TriggerKey tk = IDCPlugin.buildTriggerKey(jobInstance.getType(), jobInstance.getTaskId(), jobInstance.getGroupId());
				
				SentinelPK spk = new SentinelPK();
				spk.setShouldFireTime(IDCPlugin.toMills(nextLoadDate));
				spk.setTriggerName(tk.getName());
				spk.setTriggerGroup(tk.getGroup());
				
				// TODO 未考虑任务间的依赖
				// 更新下个周期的结果为 READY 状态
				Sentinel sentinel = sentinelRepository.findOne(spk);
				sentinel.setStatus(SentinelStatus.READY);
				sentinelRepository.save(sentinel);
			}
		}
	}
}
