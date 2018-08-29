package com.iwellmass.idc.server.quartz;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstanceType;

public class IDCPlugin implements SchedulerPlugin, IDCConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCPlugin.class);

	public static final SimpleDateFormat DEFAULT_LOAD_DATE_DF = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static final DateTimeFormatter DEFAULT_LOAD_DATE_DTF = DateTimeFormatter.ofPattern(DEFAULT_LOAD_DATE_DF.toPattern());

	public IDCPlugin() {}
	
	@Inject
	private IDCSchedulerListener idcSchedulerListener;
	
	@Inject
	private IDCTriggerListener idcTriggerListener;
	
	@Inject
	private IDCJobListener idcJobListener;
	
	@SuppressWarnings("unused")
	private DataSource dataSource; // new SpringConnectionProviderDelegate()

	@Override
	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
		LOGGER.info("加载 IDCPlugin");
		// listeners
		scheduler.getListenerManager().addSchedulerListener(idcSchedulerListener);
		scheduler.getListenerManager().addTriggerListener(idcTriggerListener);
		scheduler.getListenerManager().addJobListener(idcJobListener);
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

	public static String buildInstanceId(String taskId, String groupId, LocalDateTime loadDate) {
		return String.format("%s_%s_%s", groupId, taskId, loadDate.format(DEFAULT_LOAD_DATE_DTF));
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

}
