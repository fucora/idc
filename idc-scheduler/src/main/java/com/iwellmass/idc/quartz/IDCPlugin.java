package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_GROUP;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_ID;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_REOD;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.quartz.JobDataMap;
import org.quartz.JobPersistenceException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobPK;
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
	
	@Override
	public void fireStartEvent(StartEvent event) {
		LOGGER.info("Get event {}", event);
		// 更新实例状态

		pluginContext.updateJobInstance(event.getInstanceId(), (jobInstance)->{
			jobInstance.setStartTime(event.getStartTime());
			jobInstance.setStatus(JobInstanceStatus.RUNNING);
		});
		pluginContext.log(event.getInstanceId(), Optional.ofNullable(event.getMessage()).orElse("开始执行"));
	}

	@Override
	@Transactional
	public void fireCompleteEvent(CompleteEvent event) {
		
		LOGGER.info("更新任务状态, {}", event);
		JobInstance ins = pluginContext.getJobInstance(event.getInstanceId());
		Assert.isTrue(ins != null, "实例 %s 不存在", event.getInstanceId());
		event.setScheduledFireTime(ins.getShouldFireTime());
		try {
			jobStore.triggeredAsyncJobComplete(toJobPK(ins.getJobPK()), event);
		} catch (JobPersistenceException e) {
			throw new AppException("无法更新任务状态" + e.getMessage());
		}
		pluginContext.log(event.getInstanceId(), Optional.ofNullable(event.getMessage()).orElse("执行完毕"));
	}
	
	public void cancleJob(String jobId, String jobGroup) {
		
	}
	
	public static void setDefaultContext(IDCPluginContext pluginContext) {
		IDCPlugin.pluginContext = pluginContext;
	}

	public static JobPK toJobPK(Trigger trigger) {
		JobDataMap jdm = trigger.getJobDataMap();
		boolean isRedo = JOB_REOD.applyGet(jdm);
		if (isRedo) {
			String jobId = JOB_ID.applyGet(jdm);
			String groupId = JOB_GROUP.applyGet(jdm);
			return new JobPK(jobId, groupId);
		} else {
			return new JobPK(trigger.getKey().getName(), trigger.getKey().getGroup());
		}
	}
	public static JobPK toJobPK(TriggerKey triggerKey) {
		return new JobPK(triggerKey.getName(), triggerKey.getGroup());
	}
	public static TriggerKey toJobPK(JobPK jobKey) {
		return new TriggerKey(jobKey.getJobId(), jobKey.getJobGroup());
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
