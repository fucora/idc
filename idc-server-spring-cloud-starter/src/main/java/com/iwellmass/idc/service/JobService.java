package com.iwellmass.idc.service;

import static com.iwellmass.idc.quartz.IDCPlugin.buildJobKey;
import static com.iwellmass.idc.quartz.IDCPlugin.buildTriggerKey;
import static com.iwellmass.idc.quartz.IDCPlugin.toDate;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstanceType;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.quartz.IDCConstants;
import com.iwellmass.idc.quartz.IDCDispatcherJob;

@Service
public class JobService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

	@Inject
	private Scheduler scheduler;
	
	@Transactional
	public void schedule(Job job) throws AppException {
		
		LOGGER.info("创建调度任务 {}", job);
		
		LocalDateTime now = LocalDateTime.now();
		
		// 默认值
		job.setCreateTime(now);
		if (job.getGroupId() == null) {
			job.setGroupId(Job.DEFAULT_GROUP);
		}
		if (job.getContentType() == null) {
			job.setContentType(Job.DEFAULT_CONTENT_TYPE);
		}
		if (job.getStartTime() == null) {
			job.setStartTime(now);
		}
		ScheduleProperties sp = job.getScheduleProperties();
		sp.setScheduleType(job.getScheduleType());
		
		JobKey jobKey = buildJobKey(job.getTaskId(), job.getGroupId());
		try {
			// 添加到 scheduler
			JobDetail jobDetail = JobBuilder.newJob(IDCDispatcherJob.class)
					.withIdentity(jobKey)
					.requestRecovery()
					.storeDurably()
					.build();
			IDCConstants.IDC_JOB_VALUE.applyPut(jobDetail.getJobDataMap(), JSON.toJSONString(job));
			scheduler.addJob(jobDetail, false);
		
			// 调度 CRON 类型
			if (sp.getScheduleType() != ScheduleType.MANUAL) {
				CronExpression cronExpr = new CronExpression(toCronExpression(sp));
				TriggerKey triggerKey = buildTriggerKey(JobInstanceType.valueOf(job.getScheduleType()), job.getTaskId(), job.getGroupId());
				
				Assert.isFalse(scheduler.checkExists(triggerKey), "不可重复调度任务");
				
				Trigger trigger = TriggerBuilder.newTrigger()
						.withIdentity(triggerKey)
						.forJob(jobKey)
						.withSchedule(CronScheduleBuilder.cronSchedule(cronExpr)
								.withMisfireHandlingInstructionIgnoreMisfires())
						.startAt(toDate(job.getStartTime()))
						.endAt(toDate(job.getEndTime())).build();
				
				IDCConstants.IDC_SCHEDULE_TYPE.applyPut(trigger.getJobDataMap(), sp.getScheduleType());
				
				// 保存到 quartz
				scheduler.scheduleJob(trigger);
			}
		} catch (AppException e) {
			throw e;
		} catch (ObjectAlreadyExistsException e) {
			LOGGER.error(e.getMessage());
			throw new AppException("不可重复调度任务");
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			throw new AppException("生成 Cron 表达式时错误, " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new AppException("调度失败: " + e.getMessage());
		}
	}
	
	public void unschedule(JobPK jobKey) throws AppException {
		try {
			LOGGER.info("取消任务 {} 所有调度计划", jobKey);
			boolean result = scheduler.deleteJob(new JobKey(jobKey.getTaskId(), jobKey.getGroupId()));
			if (!result) {
				LOGGER.warn("{} 不存在的调度任务", jobKey);
			}
		} catch (SchedulerException e) {
			throw new AppException(e);
		}
		
	}

	public void complement(ComplementRequest request) {
		try {
			String taskId = request.getTaskId();
			String groupId = request.getGroupId();

			Trigger mainTrigger = scheduler.getTrigger(buildTriggerKey(JobInstanceType.CRON, taskId, groupId));
			Assert.isTrue(mainTrigger != null, "任务未提交");

			ScheduleBuilder<? extends Trigger> sbt = mainTrigger.getScheduleBuilder();

			TriggerKey triggerKey = buildTriggerKey(JobInstanceType.COMPLEMENT, taskId, groupId);

			Trigger trigger = scheduler.getTrigger(triggerKey);

			Assert.isTrue(trigger == null, "存在正在执行的补数任务");

			TriggerBuilder<?> complementTriggerBuilder = TriggerBuilder.newTrigger().withIdentity(triggerKey)
					.forJob(mainTrigger.getJobKey()).withSchedule(sbt)
					.startAt(toDate(LocalDateTime.of(request.getStartTime(), LocalTime.MIN)))
					.endAt(toDate(LocalDateTime.of(request.getEndTime(), LocalTime.MAX)));

			if (trigger == null) {
				scheduler.scheduleJob(complementTriggerBuilder.build());
			} else {
				scheduler.rescheduleJob(triggerKey, complementTriggerBuilder.build());
			}

		} catch (SchedulerException e) {
			throw new AppException("补数异常: " + e.getMessage());
		}
	}

	public void lock(JobPK jobKey) {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public void unlock(JobPK jobKey) {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public void execute(ExecutionRequest request) {
		
		String taskId = request.getTaskId();
		String groupId = request.getGroupId();
		
		TriggerKey tk = buildTriggerKey(JobInstanceType.MANUAL, taskId, groupId);
		
		try {
			TriggerState state = scheduler.getTriggerState(tk);
			
			if (state != TriggerState.NONE) {
				Assert.isTrue(state == TriggerState.COMPLETE, "不可重复执行任务");
			}
			
			JobDataMap jdm = new JobDataMap();
			IDCConstants.IDC_PARAMETER.applyPut(jdm, request.getJobParameter());
			IDCConstants.IDC_SCHEDULE_TYPE.applyPut(jdm, ScheduleType.MANUAL);
			Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(tk)
				.forJob(taskId, groupId)
				.build();
			
			
			scheduler.scheduleJob(trigger);
		} catch (SchedulerException e) {
			throw new AppException("执行失败: " + e.getMessage());
		}
	}
	
	public String toCronExpression(ScheduleProperties scheduleProperties) {
		LocalTime duetime = LocalTime.parse(scheduleProperties.getDuetime(), DateTimeFormatter.ISO_TIME);
		switch (scheduleProperties.getScheduleType()) {
		case MONTHLY: {
			List<Integer> days = scheduleProperties.getDaysOfMonth();
			Assert.isFalse(Utils.isNullOrEmpty(days), "月调度配置不能为空");
			return String.format("%s %s %s %s * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(),
					String.join(",", days.stream().map(i -> i + "").collect(Collectors.toList())));
		}
		case WEEKLY: {
			List<Integer> days = scheduleProperties.getDaysOfMonth();
			Assert.isFalse(Utils.isNullOrEmpty(days), "周调度配置不能为空");
			return String.format("%s %s %s ? * %s *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(),
					String.join(",", days.stream().map(i -> i + "").collect(Collectors.toList())));
		}
		case DAILY:
			return String.format("%s %s %s * * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour());
		default:
			throw new AppException("未指定周期调度类型, 接收的周期调度类型" + Arrays.asList(ScheduleType.values()));
		}
	}

}
