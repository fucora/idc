package com.iwellmass.idc.quartz;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstanceType;
import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.service.PluginVersionService;

@Component
public class IDCPlugin implements SchedulerPlugin, IDCConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCPlugin.class);

	@Inject
	private IDCQuartzSchedulerListener idcSchedulerListener;
	
	@Inject
	private IDCQuartzTriggerListener idcTriggerListener;
	
	@Inject
	private IDCQuartzJobListener idcJobListener;
	
	@Inject
	private PluginVersionService pluginService;
	
	@Override
	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
		LOGGER.info("加载 IDCPlugin...");
		PluginVersion version = null;
		try {
			version = pluginService.initPlugin();
		} catch (Exception e) {
			throw new SchedulerException("初始化 IDCPlugin 时出错: " + String.valueOf(e.getMessage()), e);
		}
		// listeners
		scheduler.getListenerManager().addSchedulerListener(idcSchedulerListener);
		scheduler.getListenerManager().addTriggerListener(idcTriggerListener);
		scheduler.getListenerManager().addJobListener(idcJobListener);
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

	public static JobKey toJobKey(TriggerKey triggerKey) {
		String key = triggerKey.getName();
		String jobName = key.substring(key.indexOf('_') + 1, key.length());
		return new JobKey(jobName, triggerKey.getGroup());
	}

	public static JobInstanceType getTriggerType(Trigger trigger) {
		TriggerKey tk = trigger.getKey();
		String instanceTypeStr = tk.getName().substring(0, tk.getName().indexOf('_'));
		return JobInstanceType.valueOf(instanceTypeStr);
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

	public static JobKey buildJobKey(String taskId) {
		return buildJobKey(taskId, null);
	}

	public static JobKey buildJobKey(String taskId, String groupId) {
		if (groupId == null) {
			groupId = Job.DEFAULT_GROUP;
		}
		JobKey jobKey = new JobKey(taskId, groupId);
		return jobKey;
	}

	public static TriggerKey buildTriggerKey(JobInstanceType type, String taskId) {
		return buildTriggerKey(type, taskId, null);
	}

	public static TriggerKey buildTriggerKey(JobInstanceType type, String taskId, String groupId) {
		if (groupId == null) {
			groupId = Job.DEFAULT_GROUP;
		}
		TriggerKey key = new TriggerKey(type.toString() + "_" + taskId, groupId);
		return key;
	}

	public static Long toMills(LocalDateTime loadDate) {
		return loadDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
}
