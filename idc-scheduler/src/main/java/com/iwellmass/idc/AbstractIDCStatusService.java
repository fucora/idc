package com.iwellmass.idc;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.quartz.IDCJobStore;
import com.iwellmass.idc.quartz.IDCPlugin;

import lombok.Setter;

public abstract class AbstractIDCStatusService implements IDCStatusService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIDCStatusService.class);

	@Setter
	private IDCPlugin plugin;

	@Setter
	private IDCJobStore idcStore;
	
	// ~~ 事件服务~~
	@Override
	public void fireStartEvent(StartEvent event) {
		LOGGER.info("Get event {}", event);
		plugin.getLogger().log(event.getInstanceId(), Optional.ofNullable(event.getMessage()).orElse("开始执行"));
		// 更新实例状态
		try {
			// 更新实例状态
			idcStore.storeIDCJobInstance(event.getInstanceId(), (jobInstance)->{
				jobInstance.setStartTime(event.getStartTime());
				jobInstance.setStatus(JobInstanceStatus.RUNNING);
			});
		} catch (Exception e) {
			String error = "更新任务状态出错" + e.getMessage();
			plugin.getLogger().log(event.getInstanceId(), error);
			throw new AppException(error, e);
		}
	}

	public void fireProgressEvent(ProgressEvent event) {
		LOGGER.info("Get event {}", event);
		plugin.getLogger().log(event.getInstanceId(), event.getMessage());
		// 更新实例状态
		try {
			idcStore.storeIDCJobInstance(event.getInstanceId(), (jobInstance)->{
				jobInstance.setStatus(JobInstanceStatus.RUNNING);
			});
		} catch (Exception e) {
			String error = "更新任务状态出错" + e.getMessage();
			plugin.getLogger().log(event.getInstanceId(), error);
			throw new AppException(error, e);
		}
	}
	
	@Override
	public void fireCompleteEvent(CompleteEvent event) {
		LOGGER.info("Get event {}", event);
		plugin.getLogger().log(event.getInstanceId(), event.getMessage())
			.log(event.getInstanceId(), "任务结束, 执行结果: {}", event.getFinalStatus());
		try {
			// 完成这个实例
			idcStore.completeIDCJobInstance(event);
			
			// 如果是
		} catch (Exception e) {
			String error = "更新任务状态出错: " + e.getMessage();
			plugin.getLogger().log(event.getInstanceId(), error);
			throw new AppException(error, e);
		}
	}

}
