package com.iwellmass.idc;

import java.time.LocalDateTime;
import java.util.Random;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.model.CompleteEvent;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.StartEvent;
import com.iwellmass.idc.server.rpc.JobStatusManager;
import com.iwellmass.idc.service.ExecutionRequest;
import com.iwellmass.idc.service.JobExecutorService;

@Component
public class LocalJobExecutorService implements JobExecutorService{

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalJobExecutorService.class);
	
	@Inject
	private JobStatusManager statusManager;
	
	@Override
	public void execute(ExecutionRequest executionRequest) {
		
		StartEvent startEvent = new StartEvent();
		startEvent.setStartTime(LocalDateTime.now());
		startEvent.setInstanceId(executionRequest.getInstanceId());
		startEvent.setMessage("开始执行任务...");
		
		statusManager.fireJobStart(startEvent);
		
		LOGGER.info("execution request: {}", executionRequest);
		LOGGER.info("execution parameters: {}", executionRequest.getParameters());
		
		long rand = new Random().nextInt(6) + 3;
		
		try {
			LOGGER.info("sleep {}ms ...", rand);
			Thread.sleep(rand);
		} catch (InterruptedException e) {
		}
		
		CompleteEvent event = new CompleteEvent();
		event.setInstanceId(executionRequest.getInstanceId());
		event.setFinalStatus(JobInstanceStatus.FINISHED);
		event.setEndTime(LocalDateTime.now());
		event.setMessage("执行成功");
		statusManager.fireJobComplete(event);
	}

}
