package com.iwellmass.idc.server;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.model.JobStatusEvent;
import com.iwellmass.idc.repo.JobBarrierRepository;
import com.iwellmass.idc.repo.JobBarrierRepository.SentinelCheck;

@Component
public class SentinelLoop implements Runnable {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(SentinelLoop.class);
	
	private final Thread thread;
	
	@Inject
	private JobStatusManager statusManager;

	@Inject
	private JobBarrierRepository jobExecutionBarrierRepository;
	
	
	public SentinelLoop() {
		thread = new Thread(this);
	}
	
	@PostConstruct
	public void init() {
		LOGGER.info("哨兵线程已启动");
		thread.start();
	}
	

	@Override
	public void run() {

		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				//do nothing
			}
			
			// 获取所有未执行的任务
			List<SentinelCheck> results = jobExecutionBarrierRepository.checkedSentinel();

			for (SentinelCheck result : results) {
				JobStatusEvent event = new JobStatusEvent();
				event.setInstanceId(result.getInstanceId());
				event.setMessage("前置依赖已完成，执行该任务");
				statusManager.fireJobActived(event);
				// TODO 通知任务已被通知
			}
		}

	}
}
