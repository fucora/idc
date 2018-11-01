package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_LOGGER;
import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import java.util.Optional;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.PluginVersion;

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
}
