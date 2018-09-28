package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.util.Assert;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.PluginVersion;

public class IDCPlugin implements SchedulerPlugin, IDCConstants, IDCStatusService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCPlugin.class);
	
	private static IDCPluginContext pluginContext;
	
	private IDCJobStore jobStore;
	
	public IDCPlugin(IDCJobStore jobStore) {
		this.jobStore = jobStore;
	}

	@Override
	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
		LOGGER.info("加载 IDCPlugin...");
		if (pluginContext == null) {
			throw new SchedulerException("未设置 IDCPluginContext...");
		}
		// 将其设置在上下文中
		IDC_PLUGIN.applyPut(scheduler.getContext(), this);
		
		scheduler.getListenerManager().addJobListener(new IDCJobListener(pluginContext));
		scheduler.getListenerManager().addSchedulerListener(new IDCSchedulerListener(pluginContext));
		scheduler.getListenerManager().addTriggerListener(new IDCTriggerListener(pluginContext));
		
		PluginVersion version = new PluginVersion();
		LOGGER.info("IDCPlugin 已加载, VERSION: {}", version.getVersion());
	}

	public static final <T> List<T> nullable(List<T> list) {
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public void start() {
		LOGGER.info("启动 IDCPlugin");
	}

	@Override
	public void shutdown() {
		LOGGER.info("停止 IDCPlugin");
	}
	
	public IDCPluginContext getContext() {
		return pluginContext;
	}
	
	/** 恢复等待异步结果的 trigger */
	public void completeAsyncJob(String triggerName, String triggerGroup, JobInstanceStatus finalStatus) throws SchedulerException {
		CompletedExecutionInstruction instruction = null;
		if (finalStatus == JobInstanceStatus.FAILED) {
			instruction = CompletedExecutionInstruction.SET_TRIGGER_ERROR;
		} else {
			instruction = CompletedExecutionInstruction.NOOP;
		}
		jobStore.triggeredAsyncJobComplete(new TriggerKey(triggerName, triggerGroup), instruction);
	}


	@Override
	public void fireStartEvent(StartEvent event) {
		
		LOGGER.info("Get event {}", event);
		
		// 更新实例状态
		pluginContext.updateJobInstance(event.getInstanceId(), (jobInstance)->{
			jobInstance.setStartTime(event.getStartTime());
			jobInstance.setStatus(JobInstanceStatus.RUNNING);
		});
		String message = event.getMessage();
		if (message == null) {
			message = "开始执行任务 " + event.getInstanceId();
		}
		pluginContext.log(event.getInstanceId(), message);
	}

	@Override
	public void fireCompleteEvent(CompleteEvent event) {
		
		LOGGER.info("Get {}", event);
		
		// 更新实例状态
		pluginContext.updateJobInstance(event.getInstanceId(), (jobInstance)->{
			Assert.isTrue(jobInstance != null, "无法更新实例 %s, 不存在此实例", event.getInstanceId());
			
			jobInstance.setStatus(event.getFinalStatus());
			Optional.ofNullable(jobInstance.getEndTime()).ifPresent(jobInstance::setEndTime);
			
			if (event.getFinalStatus() == JobInstanceStatus.FINISHED) {
				try {
					completeAsyncJob(jobInstance.getJobId(), jobInstance.getJobGroup(), event.getFinalStatus());
				} catch (SchedulerException e) {
					LOGGER.warn("无法更新实例 %s, %s", event.getInstanceId(), e.getMessage());
				}
			}
		});
		String message = Optional.ofNullable(event.getMessage()).orElse("执行完毕");
		pluginContext.log(event.getInstanceId(), message);
		LOGGER.info("实例 {} 更新至 {}", event.getInstanceId(), event.getFinalStatus());
	}
	
	public static void setDefaultContext(IDCPluginContext pluginContext) {
		IDCPlugin.pluginContext = pluginContext;
	}

	public static final LocalDateTime toLocalDateTime(Date date) {
		if (date == null) {
			return null;
		}
		long mill = date.getTime();
		return Instant.ofEpochMilli(mill).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static final Date toDate(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		long mill = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return new Date(mill);
	}

	public static Long toEpochMilli(LocalDateTime loadDate) {
		return loadDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

}
