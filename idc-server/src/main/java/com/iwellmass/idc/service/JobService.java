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
import java.util.Optional;
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
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.model.Assignee;
import com.iwellmass.idc.model.ComplementRequest;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstanceType;
import com.iwellmass.idc.model.JobQuery;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.quartz.IDCConstants;
import com.iwellmass.idc.quartz.IDCDispatcherJob;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.repo.JobRepository;

@Service
public class JobService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

	@Inject
	private JobRepository jobRepository;

	@Inject
	private Scheduler scheduler;

	public Job getJob(String taskId, String groupId) {
		if (groupId == null) {
			groupId = Job.DEFAULT_GROUP;
		}
		Job job = jobRepository.findOne(taskId, groupId);
		Assert.isTrue(job != null, "任务 %s.%s 不存在", groupId, taskId);
		return job;
	}

	@Transactional
	public void schedule(Job job) throws AppException {

		LocalDateTime now = LocalDateTime.now();
		job.setCreateTime(now);
		if (job.getStartTime() == null) {
			job.setStartTime(now);
		}

		try {
			JobKey jobKey = buildJobKey(job.getTaskId(), job.getGroupId());
			TriggerKey triggerKey = buildTriggerKey(JobInstanceType.valueOf(job.getScheduleType()), job.getTaskId(),
					job.getGroupId());

			ScheduleProperties sp = job.getScheduleProperties();
			sp.setScheduleType(job.getScheduleType());
			CronExpression cronExpr = new CronExpression(toCronExpression(sp));

			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey)
					.withSchedule(
							CronScheduleBuilder.cronSchedule(cronExpr).withMisfireHandlingInstructionIgnoreMisfires())
					.startAt(toDate(job.getStartTime())).endAt(toDate(job.getEndTime())).build();

			IDCConstants.CONTEXT_JOB_INSTANCE_TYPE.applyPut(trigger.getJobDataMap(), JobInstanceType.CRON);

			JobDetail jobDetail = JobBuilder.newJob(IDCDispatcherJob.class).withIdentity(jobKey).requestRecovery()
					.build();

			LocalDateTime prevFireTime = Optional.ofNullable(trigger.getPreviousFireTime())
					.map(IDCPlugin::toLocalDateTime).orElse(null);
			LocalDateTime nextFireTime = Optional.ofNullable(trigger.getNextFireTime()).map(IDCPlugin::toLocalDateTime)
					.orElse(null);
			job.setPrevLoadDate(prevFireTime);
			job.setNextLoadDate(nextFireTime);
			// 保存到 t_idc_job
			jobRepository.save(job);
			// 保存到 quartz
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AppException("生成 Cron 表达式时错误, " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new AppException("调度失败: " + e.getMessage());
		}
	}

	public void complement(ComplementRequest request) {
		try {
			String taskId = request.getTaskId();
			String groupId = request.getGroup();

			Trigger mainTrigger = scheduler.getTrigger(buildTriggerKey(JobInstanceType.CRON, taskId, groupId));
			Assert.isTrue(mainTrigger != null, "任务未提交");

			ScheduleBuilder<? extends Trigger> sbt = mainTrigger.getScheduleBuilder();

			TriggerKey triggerKey = buildTriggerKey(JobInstanceType.COMPLEMENT, taskId, groupId);

			Trigger trigger = scheduler.getTrigger(triggerKey);

			Assert.isTrue(trigger == null || hasComplete(triggerKey), "存在正在执行的补数任务");

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

	private boolean hasComplete(TriggerKey tk) throws SchedulerException {
		TriggerState state = scheduler.getTriggerState(tk);
		return state == TriggerState.COMPLETE;
	}

	public void lock(String taskId, String groupId) {
		try {
			JobKey jobKey = new JobKey(taskId, Optional.ofNullable(groupId).orElse(Job.DEFAULT_GROUP));
			scheduler.pauseJob(jobKey);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage());
		}
	}

	public void unlock(String taskId, String groupId) {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public PageData<Job> findJob(JobQuery query, Pager pager) {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public List<Job> getWorkflowJob() {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public List<Job> getWorkflowJob(Integer jobId) {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public List<Assignee> getAllAssignee() {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public void execute() {
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
