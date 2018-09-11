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
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
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
import com.iwellmass.idc.quartz.IDCPlugin;

@Service
public class JobService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

	@Inject
	private Scheduler scheduler;

	@Transactional
	public void schedule(Job job) throws AppException {
		
		LOGGER.info("创建调度任务 {}", job);
		
		LocalDateTime now = LocalDateTime.now();
		
		// 默认的
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
		
		// 添加到 scheduler
		try {
			
			CronExpression cronExpr = new CronExpression(toCronExpression(sp));
			TriggerKey triggerKey = buildTriggerKey(JobInstanceType.valueOf(job.getScheduleType()), job.getTaskId(), job.getGroupId());

			Assert.isFalse(scheduler.checkExists(triggerKey), "不可重复调度任务");
			
			JobKey jobKey = buildJobKey(job.getTaskId(), job.getGroupId());
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(triggerKey)
					.withSchedule(CronScheduleBuilder.cronSchedule(cronExpr)
					.withMisfireHandlingInstructionIgnoreMisfires())
					.startAt(toDate(job.getStartTime()))
					.endAt(toDate(job.getEndTime())).build();

			// 设置默认值
			IDCConstants.IDC_JOB_VALUE.applyPut(trigger.getJobDataMap(), JSON.toJSONString(job));
			IDCConstants.CONTEXT_JOB_INSTANCE_TYPE.applyPut(trigger.getJobDataMap(), JobInstanceType.CRON);

			JobDetail jobDetail = JobBuilder.newJob(IDCDispatcherJob.class)
					.withIdentity(jobKey)
					.requestRecovery()
					.build();
			// 保存到 quartz
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (AppException e) {
			throw e;
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AppException("生成 Cron 表达式时错误, " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new AppException("调度失败: " + e.getMessage());
		}
	}
	
	public void unschedule(JobPK jobKey) throws AppException {
		try {
			LOGGER.info("取消调度任务 {}", jobKey);
			TriggerKey triggerKey = IDCPlugin.buildTriggerKey(JobInstanceType.CRON, jobKey.getTaskId(), jobKey.getGroupId());
			boolean result = scheduler.unscheduleJob(triggerKey);
			if (!result) {
				LOGGER.warn("{} 不存在关联的调度计划", jobKey);
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
		throw new UnsupportedOperationException("not supported yet.");
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
