package com.iwellmass.idc.service;

import javax.inject.Inject;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.CancleRequest;
import com.iwellmass.idc.app.model.RedoRequest;
import com.iwellmass.idc.model.ExecutionLog;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.quartz.IDCContextKey;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.repo.ExecutionLogRepository;
import com.iwellmass.idc.repo.JobInstanceRepository;

@Service
public class JobInstanceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobInstanceService.class);
	
	@Inject
	private JobInstanceRepository jobInstanceRepository;
	
	@Inject
	private ExecutionLogRepository logRepository;

	@Inject
	private Scheduler scheduler;

	public void redo(RedoRequest request) {
		/*int instanceId = request.getInstanceId();
		JobInstance instance = jobInstanceRepository.findOne(instanceId);
		if (instance == null) {
			throw new AppException("任务实例 '" + instanceId + "' 不存在");
		}

		JobKey jobKey = new JobKey(instance.getTaskId(), instance.getGroupId());
		TriggerKey triggerKey = IDCPlugin.buildTriggerKeyForRedo(instanceId);

		JobDataMap jdm = new JobDataMap();
		CONTEXT_INSTANCE_ID.applyPut(jdm, instanceId);

		Trigger trigger = TriggerBuilder.newTrigger().forJob(jobKey).usingJobData(jdm).startNow()
				.withIdentity(triggerKey).withSchedule(SimpleScheduleBuilder.simpleSchedule()).build();

		try {
			TriggerState state = scheduler.getTriggerState(triggerKey);

			switch (state) {
			case NONE:
				scheduler.scheduleJob(trigger);
				break;
			case COMPLETE:
			case ERROR:
			case BLOCKED:
				scheduler.rescheduleJob(triggerKey, trigger);
				break;
			case NORMAL:
			case PAUSED:
				throw new SchedulerException("任务正在执行");
			}
		} catch (SchedulerException e) {
			throw new AppException("重跑失败: " + e.getMessage(), e);
		}*/
	}

	public void cancle(CancleRequest req) {
		int instanceId = req.getInstanceId();
		JobInstance instance = jobInstanceRepository.findOne(instanceId);
		Assert.isTrue(instance != null, "不存在此实例 %s", instanceId);
		
		try {
			IDCPlugin plugin = IDCContextKey.IDC_PLUGIN.applyGet(scheduler.getContext());
			plugin.cancleJob(instance.getJobId(), instance.getJobGroup());
			
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
			logRepository.log(instanceId, "无法取消任务: {}", e.getMessage());
			if (req.isForce()) {
				instance.setStatus(JobInstanceStatus.CANCLED);
				logRepository.log(instanceId, "强制取消任务", e.getMessage());
			} else {
				throw new AppException("无法取消任务: " + e.getMessage(), e);
			}
		}
	}
	
	public PageData<ExecutionLog> getJobInstanceLog(Integer id, Pager pager) {
		Pageable page = new PageRequest(pager.getPage(), pager.getLimit());
		Page<ExecutionLog> data = logRepository.findByInstanceId(id, page);
		return new PageData<>((int) data.getTotalElements(), data.getContent());
	}

}
