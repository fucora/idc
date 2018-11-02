package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_LOGGER;
import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_JSON;
import static com.iwellmass.idc.quartz.IDCUtils.toDate;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobPersistenceException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
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
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleType;

public class IDCPlugin implements SchedulerPlugin, IDCConstants, IDCStatusService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCPlugin.class);
	
	private IDCJobStoreTX idcJobStore;
	
	private IDCLogger idcLogger;
	
	public IDCPlugin(IDCJobStoreTX jobStore) {
		this.idcJobStore = jobStore;
	}

	@Override
	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
		LOGGER.info("加载 IDCPlugin...");
		// 将其设置在上下文中
		IDC_PLUGIN.applyPut(scheduler.getContext(), this);
		
		scheduler.getListenerManager().addJobListener(new IDCJobListener());
		scheduler.getListenerManager().addSchedulerListener(new IDCSchedulerListener());
		scheduler.getListenerManager().addTriggerListener(new IDCTriggerListener());
		
		idcLogger = IDC_LOGGER.applyGet(scheduler);
		
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
	
	// ~~ 任务服务 ~~
	public Trigger buildTrigger(Job job, boolean updateJob) throws ParseException {
		// Job 
		JobDataMap jobData = new JobDataMap();
		if (updateJob) {
			JOB_JSON.applyPut(jobData, JSON.toJSONString(job));
		}
		
		if (job.getDispatchType() == DispatchType.AUTO) {
			TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
					.withIdentity(job.getJobId(), job.getJobGroup())
					.forJob(job.getTaskId(), job.getGroupId())
					.usingJobData(jobData)
					.withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(toCronExpression(job.getScheduleProperties())))
							.withMisfireHandlingInstructionIgnoreMisfires());
			// 有效期起
			if (job.getStartTime() != null) {
				triggerBuilder.startAt(toDate(job.getStartTime()));
			}
			// 有效期止
			if (job.getEndTime() != null) {
				triggerBuilder.endAt(toDate(job.getEndTime()));
			}
			return triggerBuilder.build();
		} else {
			Trigger trigger = TriggerBuilder.newTrigger()
				.usingJobData(jobData)
				.withIdentity(job.getJobId(), job.getJobGroup())
				.forJob(job.getTaskId(), job.getGroupId()).build();
			return trigger;
		}
	}
	
	public static String toCronExpression(ScheduleProperties scheduleProperties) {
		LocalTime duetime = LocalTime.parse(scheduleProperties.getDuetime(), DateTimeFormatter.ISO_TIME);
		switch (scheduleProperties.getScheduleType()) {
		case MONTHLY: {
			List<Integer> days = scheduleProperties.getDays();
			Assert.isFalse(Utils.isNullOrEmpty(days), "月调度配置不能为空");
			
			boolean isLast = days.stream().filter(i -> i < 0).count() == 1;
			if(isLast && days.size() > 1) {
				throw new AppException("最后 T 天不能使用组合配置模式");
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
	@Override
	public void fireStartEvent(StartEvent event) {
		LOGGER.info("Get event {}", event);
		idcLogger.log(event.getInstanceId(), Optional.ofNullable(event.getMessage()).orElse("开始执行"));
		// 更新实例状态
		try {
			// 更新实例状态
			idcJobStore.updateJobInstance(event.getInstanceId(), (jobInstance)->{
				jobInstance.setStartTime(event.getStartTime());
				jobInstance.setStatus(JobInstanceStatus.RUNNING);
			});
		} catch (Exception e) {
			String error = "更新任务状态出错" + e.getMessage();
			idcLogger.log(event.getInstanceId(), error);
			throw new AppException(error, e);
		}
	}

	public void fireProgressEvent(ProgressEvent event) {
		LOGGER.info("Get event {}", event);
		idcLogger.log(event.getInstanceId(), event.getMessage());
		
		// 更新实例状态
		try {
			idcJobStore.updateJobInstance(event.getInstanceId(), (jobInstance)->{
				jobInstance.setStatus(JobInstanceStatus.RUNNING);
			});
		} catch (Exception e) {
			String error = "更新任务状态出错" + e.getMessage();
			idcLogger.log(event.getInstanceId(), error);
			throw new AppException(error, e);
		}
	}
	
	@Override
	public void fireCompleteEvent(CompleteEvent event) {
		LOGGER.info("Get event {}", event);
		idcLogger.log(event.getInstanceId(), event.getMessage())
			.log(event.getInstanceId(), "任务结束, 执行结果: {}", event.getFinalStatus());
		try {
			// 更新实例状态
			idcJobStore.triggeredAsyncJobComplete(event);
		} catch (Exception e) {
			String error = "更新任务状态出错" + e.getMessage();
			idcLogger.log(event.getInstanceId(), error);
			throw new AppException(error, e);
		}
	}

	public void addJob(Job job) {
		
		// check dependencies
	
		
		
		try {
			idcJobStore.storeIdcJob(job);
		} catch (JobPersistenceException e) {
			throw new AppException("无法保存任务信息: " + e.getMessage());
		}
	}
}
