package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.IDCLogger;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.SimpleIDCLogger;
import com.iwellmass.idc.dag.WorkflowService;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskType;

import lombok.Getter;
import lombok.Setter;

public abstract class IDCPlugin implements SchedulerPlugin, IDCConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCPlugin.class);

	// ~~ init from factory ~~
	private IDCJobStore idcJobStore;
	private Scheduler scheduler;
	
	// ~~ internal component ~~
	private IDCStatusService idcStatusService;
	
	// ~~ set ~~
	@Setter
	@Getter
	private WorkflowService workflowService;
	
	@Setter
	@Getter
	private IDCLogger logger;
	
	public void initialize(IDCJobStore store) {
		this.idcJobStore = store;
		this.logger = new SimpleIDCLogger();
	}
	
	@Override
	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
		LOGGER.info("加载 IDCPlugin...");
		
		Objects.requireNonNull(idcJobStore, "IDCJobStore cannot be null");
		Objects.requireNonNull(workflowService, "WorkflowService cannot be null");

		// set
		this.idcStatusService = new StdStatusService();
		this.scheduler = scheduler;
		
		// 将其设置在上下文中
		IDC_PLUGIN.applyPut(scheduler.getContext(), this);
		
		scheduler.getListenerManager().addJobListener(new IDCJobListener());
		scheduler.getListenerManager().addTriggerListener(new IDCTriggerListener(idcJobStore));
		scheduler.getListenerManager().addSchedulerListener(new IDCSchedulerListener());
		
		PluginVersion version = new PluginVersion();
		LOGGER.info("IDCPlugin 已加载, VERSION: {}", version.getVersion());
	}

	@Override
	public void start() {
		LOGGER.info("启动 IDCPlugin");
	}

	@Override
	public void shutdown() {
		LOGGER.info("停止 IDCPlugin");
	}
	
	/** 调度任务 */
	public void schedule(Task task, ScheduleProperties schdProps) throws SchedulerException {
		JobDetail jobDetail = null;
		// 工作流任务
		if (task.getTaskType() == TaskType.WORKFLOW_TASK) {
			 jobDetail = JobBuilder.newJob(IDCWorkflowJob.class)
				 .withIdentity(task.getTaskId(), task.getTaskGroup())
				 .storeDurably()
				 .requestRecovery()
				 .build();
		} else {
			jobDetail = buildJobDetail(task);
		}
		
		TASK_JSON.applyPut(jobDetail.getJobDataMap(), JSON.toJSONString(task));
		
		
		
		Trigger trigger = buildTrigger(task, schdProps);
		// 调度此任务
		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	/** 重新调度任务 */
	public void reschedule(Job job) throws SchedulerException {
		
		/*JobKey jobPk = idcPlugin.buildJobKey(job);
		
		validate(jobPk, job.getDependencies());

		// 没有正在执行的任务计划便可以重新调度计划任务
		Job pj = jobRepository.findOne(jobPk);
		if (pj != null && pj.getStatus() != ScheduleStatus.NONE) {
			Assert.isTrue(pj.getStatus() == ScheduleStatus.PAUSED, "任务未冻结");
		}
		
		job.setJobKey(jobPk);
		job.setUpdateTime(LocalDateTime.now());

		LOGGER.info("重新调度任务 {}", jobPk);*/
		throw new SchedulerException("Not supported yet.");
	}
	
	/** 重新调度任务 */
	public void reschedule(Job job, JobDataMap jobData) throws SchedulerException {
		
		/*JobKey jobPk = idcPlugin.buildJobKey(job);
		
		validate(jobPk, job.getDependencies());

		// 没有正在执行的任务计划便可以重新调度计划任务
		Job pj = jobRepository.findOne(jobPk);
		if (pj != null && pj.getStatus() != ScheduleStatus.NONE) {
			Assert.isTrue(pj.getStatus() == ScheduleStatus.PAUSED, "任务未冻结");
		}
		
		job.setJobKey(jobPk);
		job.setUpdateTime(LocalDateTime.now());

		LOGGER.info("重新调度任务 {}", jobPk);*/
		throw new SchedulerException("Not supported yet.");
	}
	
	protected abstract JobDetail buildJobDetail(Task task);

	protected Trigger buildTrigger(Task task, ScheduleProperties sp) {
		
		JobDataMap jobData = new JobDataMap();
		JOB_SCHEDULE_PROPERTIES.applyPut(jobData, JSON.toJSONString(sp));
		
		if (task.getTaskType() == TaskType.WORKFLOW_SUB_TASK) {
			JobKey jobKey = new JobKey(task.getTaskId(), task.getTaskGroup());
			// 构建常量
			TriggerBuilder<SimpleTrigger> builder = TriggerBuilder.newTrigger()
				.withIdentity(IDCUtils.asTriggerKey(jobKey))
				.forJob(task.getTaskId(), task.getTaskGroup())
				.usingJobData(jobData)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule());
			return builder.build();
		} else {
			JobKey jobKey = new JobKey(task.getTaskId(), task.getTaskGroup());
			// 构建 CRON 表达式
			CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(toCronExpression(sp))
					.withMisfireHandlingInstructionIgnoreMisfires();
			// 构建 TriggerBuilder
			TriggerBuilder<CronTrigger> builder = TriggerBuilder.newTrigger()
					.withIdentity(IDCUtils.asTriggerKey(jobKey))
					.forJob(task.getTaskId(), task.getTaskGroup())
					.usingJobData(jobData) // IDCJob JSON
					.withSchedule(cronBuilder);
			// 设置开始时间
			Optional.ofNullable(sp.getStartTime()).map(IDCUtils::toDate).ifPresent(builder::startAt);
			// 设置结束时间
			Optional.ofNullable(sp.getEndTime()).map(IDCUtils::toDate).ifPresent(builder::endAt);
			return builder.build();
		}
	}
	
	public IDCStatusService getStatusService() {
		return this.idcStatusService;
	}
	
	public static String toCronExpression(ScheduleProperties scheduleProperties) {
		LocalTime duetime = LocalTime.parse(scheduleProperties.getDuetime(), DateTimeFormatter.ISO_TIME);
		switch (scheduleProperties.getScheduleType()) {
		case MONTHLY: {
			List<Integer> days = scheduleProperties.getDays();
			Assert.isFalse(Utils.isNullOrEmpty(days), "月调度配置不能为空");
			
			boolean isLast = days.stream().filter(i -> i < 0).count() == 1;
			if(isLast && days.size() > 1) {
				throw new AppException("最后 N 天不能使用组合配置模式");
			};
			
			return String.format("%s %s %s %s * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(),
				isLast ? days.get(0) == -1 ? "L" : "L" + (days.get(0) + 1)
					: String.join(",", days.stream().map(String::valueOf).collect(Collectors.toList())));
		}
		case WEEKLY: {
			throw new UnsupportedOperationException("not supported yet");
		}
		case DAILY:
			return String.format("%s %s %s * * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour());
		default:
			throw new AppException("未指定周期调度类型, 接收的周期调度类型" + Arrays.asList(ScheduleType.values()));
		}
	}
	
	// ~~ 事件服务~~
	private class StdStatusService implements IDCStatusService {
		@Override
		public void fireStartEvent(StartEvent event) {
			LOGGER.info("Get event {}", event);
			logger.log(event.getInstanceId(), Optional.ofNullable(event.getMessage()).orElse("开始执行"));
			// 更新实例状态
			try {
				// 更新实例状态
				idcJobStore.storeIDCJobInstance(event.getInstanceId(), (jobInstance)->{
					jobInstance.setStartTime(event.getStartTime());
					jobInstance.setStatus(JobInstanceStatus.RUNNING);
				});
			} catch (Exception e) {
				String error = "更新任务状态出错" + e.getMessage();
				logger.log(event.getInstanceId(), error);
				throw new AppException(error, e);
			}
		}

		public void fireProgressEvent(ProgressEvent event) {
			LOGGER.info("Get event {}", event);
			logger.log(event.getInstanceId(), event.getMessage());
			// 更新实例状态
			try {
				idcJobStore.storeIDCJobInstance(event.getInstanceId(), (jobInstance)->{
					jobInstance.setStatus(JobInstanceStatus.RUNNING);
				});
			} catch (Exception e) {
				String error = "更新任务状态出错" + e.getMessage();
				logger.log(event.getInstanceId(), error);
				throw new AppException(error, e);
			}
		}
		
		@Override
		public void fireCompleteEvent(CompleteEvent event) {
			LOGGER.info("Get event {}", event);
			logger.log(event.getInstanceId(), event.getMessage())
				.log(event.getInstanceId(), "任务结束, 执行结果: {}", event.getFinalStatus());
			try {
				// 完成这个实例
				idcJobStore.completeIDCJobInstance(event);
			} catch (Exception e) {
				String error = "更新任务状态出错: " + e.getMessage();
				logger.log(event.getInstanceId(), error);
				throw new AppException(error, e);
			}
		}
	}
}
