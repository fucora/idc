package com.iwellmass.idc.service;

import javax.inject.Inject;

import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.repo.JobInstanceRepository;

@Service
public class JobInstanceService {

	@Inject
	private JobInstanceRepository jobInstanceRepository;

	@Inject
	private Scheduler scheduler;

	public void redo(Integer instanceId) {
		JobInstance instance = jobInstanceRepository.findOne(instanceId);
		if (instance == null) {
			throw new AppException("重跑失败, 任务实例 '" + instanceId + "' 不存在");
		}
		
		

		JobKey jobKey = new JobKey(instance.getTaskId(), instance.getGroupId());
		TriggerKey triggerKey = IDCPlugin.buildTriggerKeyForRedo(instanceId);

		JobDataMap jdm = new JobDataMap();
		IDCPlugin.CONTEXT_INSTANCE_ID.applyPut(jdm, instanceId);
		IDCPlugin.CONTEXT_REDO.applyPut(jdm, true);

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
		}
	}

}
