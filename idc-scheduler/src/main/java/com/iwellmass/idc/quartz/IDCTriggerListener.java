package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE_ID;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCPlugin.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobInstanceType;

public class IDCTriggerListener extends TriggerListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCTriggerListener.class);

	private final IDCPluginContext pluginContext;
	
	public IDCTriggerListener(IDCPluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		
		DispatchType type = null;
		
		JobKey jobKey = trigger.getJobKey();

		boolean isRedo = false;

		JobInstance instance = null;
		
		if (context.isRecovering()) {
			// TODO
			throw new UnsupportedOperationException("not supported yet.");
		} else if (isRedo) {
			throw new UnsupportedOperationException("not supported yet.");
			/*int id = IDCConstants.CONTEXT_INSTANCE_ID.applyGet(context);
			JobInstance jobInstance = jobInstanceRepository.findOne(id);
			jobInstance.setStatus(JobInstanceStatus.NEW);
			jobInstance.setStartTime(LocalDateTime.now());
			jobInstance.setEndTime(null);
			instance = jobInstanceRepository.save(jobInstance);*/
		} else if (type == DispatchType.MANUAL) {
			
			LocalDateTime loadDate = CONTEXT_LOAD_DATE.applyGet(trigger.getJobDataMap());
			
			instance = pluginContext.createJobInstance(jobKey, (job) -> {
				JobInstance jobInstance = createJobInstance(job);
				jobInstance.setInstanceType(JobInstanceType.CRON);
				jobInstance.setLoadDate(loadDate);
				jobInstance.setNextLoadDate(null);
				jobInstance.setShouldFireTime(IDCPlugin.toMills(loadDate));
				return jobInstance;
			});
		} else {
			
			Date shouldFireTime = context.getScheduledFireTime();
			Date nextFireTime = context.getNextFireTime();
			
			instance = pluginContext.createJobInstance(jobKey, (job) -> {
				JobInstance jobInstance = createJobInstance(job);
				jobInstance.setInstanceType(JobInstanceType.CRON);
				jobInstance.setLoadDate(toLocalDateTime(shouldFireTime));
				jobInstance.setNextLoadDate(toLocalDateTime(nextFireTime));
				jobInstance.setShouldFireTime(shouldFireTime == null ? -1 : shouldFireTime.getTime());
				return jobInstance;
			});
		}

		// 初始化执行环境
		CONTEXT_LOAD_DATE.applyPut(context, instance.getLoadDate());
		CONTEXT_INSTANCE_ID.applyPut(context, instance.getInstanceId());
	}

	/* 手动触发，必须传入业务日期 */
//	private JobInstance triggerManual(Trigger trigger, JobExecutionContext context, Job job) {
//		TriggerKey tk = trigger.getKey();
//		LocalDateTime loadDate = CONTEXT_LOAD_DATE.applyGet(trigger.getJobDataMap());
//		JobInstance jobInstance = getOrCreateInstance(job, loadDate);
//		jobInstance.setType(job.getScheduleType());
//		jobInstance.setNextFireTime(-1L);
//		jobInstance.setTriggerName(tk.getName());
//		pluginContext.updateJobInstance(jobInstance);
//		LOGGER.info("创建 {} 任务实例 {}", trigger.getJobKey(), jobInstance.getInstanceId());
//		return jobInstance;
//	}

	private JobInstance createJobInstance(Job job) {
		JobInstance jobInstance = new JobInstance();
		jobInstance.setTaskId(job.getTaskId());
		jobInstance.setGroupId(job.getGroupId());
		jobInstance.setTaskName(jobInstance.getTaskName());
		jobInstance.setContentType(job.getContentType());
		jobInstance.setTaskType(job.getTaskType());
		jobInstance.setAssignee(job.getAssignee());
		jobInstance.setTaskType(job.getTaskType());
		jobInstance.setStatus(JobInstanceStatus.NEW);
		jobInstance.setParameter(job.getParameter());
		jobInstance.setStartTime(LocalDateTime.now());
		jobInstance.setEndTime(null);
		return jobInstance;
	}

	@Override
	public String getName() {
		return IDCTriggerListener.class.getName();
	}
}
