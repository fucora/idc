package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.model.ScheduleType;

public class IDCPlugin implements SchedulerPlugin, IDCConstants {

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
		
		// 将其设置再上下文中
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
	
	public static void setDefaultContext(IDCPluginContext pluginContext) {
		IDCPlugin.pluginContext = pluginContext;
	}

	public static JobKey toJobKey(TriggerKey triggerKey) {
		String key = triggerKey.getName();
		String jobName = key.substring(key.indexOf('_') + 1, key.length());
		return new JobKey(jobName, triggerKey.getGroup());
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

	public static TriggerKey buildManualTriggerKey(LocalDateTime loadDate, String taskId, String groupId) {
		if (groupId == null) {
			groupId = Job.DEFAULT_GROUP;
		}
		TriggerKey key = new TriggerKey("MANUAL_" + taskId + "_" + loadDate.format(DEFAULT_LOAD_DATE_DTF), groupId);
		return key;
	}
	public static TriggerKey buildCronTriggerKey(ScheduleType type, String taskId, String groupId) {
		if (groupId == null) {
			groupId = Job.DEFAULT_GROUP;
		}
		TriggerKey key = new TriggerKey(type.toString() + "_" + taskId, groupId);
		return key;
	}

	public static Long toMills(LocalDateTime loadDate) {
		return loadDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	public static TriggerKey buildTriggerKeyForRedo(Integer instanceId) {
		TriggerKey key = new TriggerKey("REDO_" + instanceId);
		return key;
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

}
