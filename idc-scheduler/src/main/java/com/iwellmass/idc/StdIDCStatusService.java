package com.iwellmass.idc;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.quartz.IDCStore;

import lombok.Setter;

public class StdIDCStatusService implements IDCStatusService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StdIDCStatusService.class);

	@Setter
	private IDCLogger idcLogger;

	@Setter
	private IDCStore idcStore;
	
	// ~~ 事件服务~~
	@Override
	public void fireStartEvent(StartEvent event) {
		LOGGER.info("Get event {}", event);
		idcLogger.log(event.getInstanceId(), Optional.ofNullable(event.getMessage()).orElse("开始执行"));
		// 更新实例状态
		try {
			// 更新实例状态
			idcStore.storeIDCJobInstance(event.getInstanceId(), (jobInstance)->{
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
			idcStore.storeIDCJobInstance(event.getInstanceId(), (jobInstance)->{
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
			
			JobInstance ins = idcStore.retrieveIDCJobInstance(event.getInstanceId());
			
			Job job = idcStore.retrieveIDCJob(ins.getJobKey());
			ScheduleProperties sp = job.getScheduleProperties();
			
			// 更新实例状态
			if (!sp.getBlockOnError() ||
					event.getFinalStatus() == JobInstanceStatus.FINISHED) {
				idcStore.removeIDCJobBarriers(ins.getJobId(), ins.getJobGroup(), ins.getShouldFireTime());
			}
		} catch (Exception e) {
			String error = "更新任务状态出错: " + e.getMessage();
			idcLogger.log(event.getInstanceId(), error);
			throw new AppException(error, e);
		}
	}

}
