package com.iwellmass.idc.service;

import static com.iwellmass.idc.quartz.IDCContextKey.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.RedoRequest;
import com.iwellmass.idc.model.ExecutionLog;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.repo.ExecutionLogRepository;
import com.iwellmass.idc.repo.JobInstanceRepository;

@Service
public class JobInstanceService {

	@Inject
	private JobInstanceRepository jobInstanceRepository;
	
	@Inject
	private ExecutionLogRepository logRepository;

	@Inject
	private Scheduler scheduler;

	public void redo(RedoRequest request) {
		throw new UnsupportedOperationException("unsupported yet.");
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

	public PageData<ExecutionLog> getJobInstanceLog(Integer id, Pager pager) {
		Pageable page = new PageRequest(pager.getPage(), pager.getLimit());
		Page<ExecutionLog> data = logRepository.findByInstanceId(id, page);
		return new PageData<>((int) data.getTotalElements(), data.getContent());
	}

}
