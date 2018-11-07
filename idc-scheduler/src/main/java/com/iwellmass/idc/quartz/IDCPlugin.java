package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

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
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
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
import com.iwellmass.idc.dag.Workflow;
import com.iwellmass.idc.dag.WorkflowService;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

import lombok.Getter;
import lombok.Setter;

public abstract class IDCPlugin implements SchedulerPlugin, IDCConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCPlugin.class);
	
	private Scheduler scheduler;
	
	protected IDCStore store;
	
	@Getter
	private IDCStatusService statusService;
	@Getter
	private WorkflowService workflowService;
	
	@Getter
	@Setter
	protected IDCLogger logger = new SimpleIDCLogger();
	
	
	public void initialize(IDCStore store) {
		this.store = store;
	}
	
	@Override
	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
		LOGGER.info("加载 IDCPlugin...");
		this.scheduler = scheduler;
		
		// 将其设置在上下文中
		IDC_PLUGIN.applyPut(scheduler.getContext(), this);
		
		scheduler.getListenerManager().addJobListener(new IDCJobListener());
		scheduler.getListenerManager().addSchedulerListener(new IDCSchedulerListener());
		scheduler.getListenerManager().addTriggerListener(new IDCTriggerListener());
		
		PluginVersion version = new PluginVersion();
		LOGGER.info("IDCPlugin 已加载, VERSION: {}", version.getVersion());
		
		this.statusService = initIDCStatusService();
		this.workflowService = initWorkflowService();
	}

	@Override
	public void start() {
		LOGGER.info("启动 IDCPlugin");
		Objects.requireNonNull(store, "未设置 IDCStore..");
		Objects.requireNonNull(statusService, "状态服务不能为空");
		Objects.requireNonNull(workflowService, "工作流服务不能为空");
	}

	@Override
	public void shutdown() {
		LOGGER.info("停止 IDCPlugin");
	}
	
	
	/** 调度任务 */
	public void schedule(Job job) throws SchedulerException {
		if (job.getTaskType() == TaskType.WORKFLOW) {
			 TaskKey key = job.getTaskKey();
			 Workflow workflow = workflowService.getWorkflow(key);
			 List<Job> subJobs = workflow.getAllSubJob();
			 // 保存子任务
			 for (Job subJob : subJobs) {
				 JobDetail jdt = buildJobDetail(subJob);
				 scheduler.addJob(jdt, true);
			 }
			 // 保存主任务
			 JobDetail jdt = JobBuilder
				.newJob(IDCWorkflowJob.class)
				.withIdentity(job.getTaskId(), job.getGroupId())
				.requestRecovery()
				.storeDurably().build();
			 scheduler.addJob(jdt, true);
		} else {
			JobDetail jdt = buildJobDetail(job);
			scheduler.addJob(jdt, true);
		}
		
		// 调度此任务
		scheduler.scheduleJob(buildTrigger(job));
	}
	
	/** 调度子任务 */
	public void scheduleSubJob(Job mainJob, Job subJob) throws SchedulerException {
		Trigger trigger = TriggerBuilder.newTrigger()
			.forJob(mainJob.getTaskId(), mainJob.getGroupId())
			.withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionIgnoreMisfires())
			.withIdentity(subJob.getJobId(), subJob.getJobGroup()).build();
		scheduler.scheduleJob(trigger);
	}
	
	protected abstract JobDetail buildJobDetail(Job job);
	protected abstract IDCStatusService initIDCStatusService();
	protected abstract WorkflowService initWorkflowService();

	private Trigger buildTrigger(Job job) {
		ScheduleProperties sp = job.getScheduleProperties();
		// 构建 CRON 表达式
		CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(toCronExpression(sp))
			.withMisfireHandlingInstructionIgnoreMisfires();
		// 构建 TriggerBuilder
		TriggerBuilder<CronTrigger> builder = TriggerBuilder.newTrigger()
			.withIdentity(job.getJobId(), job.getJobGroup())
			.forJob(job.getTaskId(), job.getGroupId())
			.usingJobData(IDCContextKey.JOB_JSON.key(), JSON.toJSONString(job)) // IDCJob JSON
			.withSchedule(cronBuilder);
		// 设置开始时间
		Optional.ofNullable(sp.getStartTime()).map(IDCUtils::toDate).ifPresent(builder::startAt);
		// 设置结束时间
		Optional.ofNullable(sp.getEndTime()).map(IDCUtils::toDate).ifPresent(builder::endAt);
		
		
		return builder.build();
	};
	
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
	

	// ~~ Getters ~~
	public JobInstance getJobInstance(JobKey jobKey, Long shouldFireTime) {
		return this.store.retrieveIDCJobInstance(jobKey, shouldFireTime);
	}
}
