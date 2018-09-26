package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE_ID;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_DISPATCH_TYPE;
import static com.iwellmass.idc.quartz.IDCPlugin.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
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
		DispatchType type = JOB_DISPATCH_TYPE.applyGet(context.getJobDetail().getJobDataMap());
		if (type == DispatchType.AUTO) {
			initContextForAuto(trigger, context);
		} else if (type == DispatchType.MANUAL) {
			initContextForManual(trigger, context);
		} else {
			// should never
			context.setResult(CompletedExecutionInstruction.SET_TRIGGER_ERROR);
			throw new UnsupportedOperationException("not supported " + type + " dispatch type");
		}
	}

	private void initContextForManual(Trigger trigger, JobExecutionContext context) {
		LocalDateTime loadDate = CONTEXT_LOAD_DATE.applyGet(context);
		LOGGER.info("触发手动任务 {}, full loadDate {}", trigger.getJobKey(), loadDate.format(IDCPlugin.DEFAULT_LOAD_DATE_DTF));
		JobInstance instance = pluginContext.createJobInstance(trigger.getJobKey(), (job) -> {
			JobInstance jobInstance = createJobInstance(job);
			jobInstance.setTriggerName(trigger.getKey().getName());
			jobInstance.setInstanceType(JobInstanceType.MANUAL);
			jobInstance.setLoadDate(loadDate);
			jobInstance.setNextLoadDate(null);
			jobInstance.setShouldFireTime(IDCPlugin.toMills(loadDate));
			return jobInstance;
		});

		// 初始化执行环境
		CONTEXT_INSTANCE_ID.applyPut(context, instance.getInstanceId());
		CONTEXT_INSTANCE.applyPut(context, instance);
	}

	private void initContextForAuto(Trigger trigger, JobExecutionContext context) {

		Date shouldFireTime = context.getScheduledFireTime();
		Date nextFireTime = context.getNextFireTime();

		LocalDateTime loadDate = toLocalDateTime(shouldFireTime);
		LOGGER.info("触发任务 {}, full loadDate {}", trigger.getJobKey(), loadDate.format(IDCPlugin.DEFAULT_LOAD_DATE_DTF));

		JobInstance instance = pluginContext.createJobInstance(trigger.getJobKey(), (job) -> {
			JobInstance jobInstance = createJobInstance(job);
			jobInstance.setTriggerName(trigger.getKey().getName());
			jobInstance.setInstanceType(JobInstanceType.CRON);
			jobInstance.setLoadDate(loadDate);
			jobInstance.setNextLoadDate(toLocalDateTime(nextFireTime));
			jobInstance.setShouldFireTime(shouldFireTime == null ? -1 : shouldFireTime.getTime());
			return jobInstance;
		});

		// 初始化执行环境
		CONTEXT_LOAD_DATE.applyPut(context, instance.getLoadDate());
		CONTEXT_INSTANCE_ID.applyPut(context, instance.getInstanceId());
		CONTEXT_INSTANCE.applyPut(context, instance);
	}

	private JobInstance createJobInstance(Job job) {
		JobInstance jobInstance = new JobInstance();
		jobInstance.setTaskId(job.getTaskId());
		jobInstance.setGroupId(job.getGroupId());
		jobInstance.setTaskName(job.getTaskName());
		jobInstance.setContentType(job.getContentType());
		jobInstance.setTaskType(job.getTaskType());
		jobInstance.setAssignee(job.getAssignee());
		jobInstance.setTaskType(job.getTaskType());
		jobInstance.setStatus(JobInstanceStatus.NEW);
		jobInstance.setParameter(job.getParameter());
		jobInstance.setStartTime(LocalDateTime.now());
		jobInstance.setEndTime(null);
		jobInstance.setScheduleType(job.getScheduleType());
		return jobInstance;
	}

	@Override
	public String getName() {
		return IDCTriggerListener.class.getName();
	}
}
