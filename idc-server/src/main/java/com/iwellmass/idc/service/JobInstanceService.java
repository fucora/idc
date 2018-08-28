package com.iwellmass.idc.service;

import java.util.List;

import javax.inject.Inject;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.server.quartz.IDCPlugin;

@Service
public class JobInstanceService {

	@Inject
	private JobInstanceRepository repository;

	@Inject
	private Scheduler scheduler;

	public PageData<JobInstance> findJobInstance(JobInstanceQuery query, Pager pager) {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public JobInstance getJobInstance(String id) {
		JobInstance instance = repository.findOne(id);
		Assert.isTrue(instance != null, "任务实例 %s 不存在", id);
		return instance;
	}

	public void redo(String id) {
		
		JobInstance instance = getJobInstance(id);
		TriggerKey triggerKey = IDCPlugin.buildTriggerKey(instance.getType(), instance.getTaskId(), instance.getGroupId());
		
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow()).build();
		
		// TODO 设置参数
		
		try {
			TriggerState state = scheduler.getTriggerState(triggerKey);
			if (isJobComplete(state)) {
				scheduler.rescheduleJob(triggerKey, trigger);
			} else {
				throw new AppException("重跑失败:  任务已经执行");
			}
		} catch (SchedulerException e) {
			throw new AppException("重跑失败:  " + e.getMessage());
		}
	}

	public List<JobInstance> getWorkflowSubInstance(Integer id) {
		throw new UnsupportedOperationException("not supported yet.");
	}

	private boolean isJobComplete(TriggerState state) {
		return state == TriggerState.COMPLETE || state == TriggerState.ERROR;
	}

}
