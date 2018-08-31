package com.iwellmass.idc.dag;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.model.JobStatusEvent;
import com.iwellmass.idc.repo.JobRepository;
import com.iwellmass.idc.repo.SentinelRepository;
import com.iwellmass.idc.repo.SentinelRepository.SentinelCheck;
import com.iwellmass.idc.service.JobStatusManager;

@Component
public class SentinelThread implements Runnable {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(SentinelThread.class);
	
	private final Thread thread;
	
	@Inject
	private JobStatusManager statusManager;

	@Inject
	private SentinelRepository sentinelRepository;
	
	@Inject
	private JobRepository jobRepository;
	
	
	public SentinelThread() {
		thread = new Thread(this);
	}
	
	@PostConstruct
	public void init() {
		LOGGER.info("哨兵线程已启动");	
		thread.start();
	}
	

	@Override
	public void run() {
		
		

//		while (true) {
//
//			
//			
//			
//			
//			
//			
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				//do nothing
//			}
//			
//			// 获取所有未执行的任务
//			List<SentinelCheck> results = sentinelRepository.sentinelCheck();
//
//			for (SentinelCheck result : results) {
//				JobStatusEvent event = new JobStatusEvent();
//				//event.setInstanceId(result.getInstanceId());
//				event.setMessage("前置依赖已完成，执行该任务");
//				statusManager.fireJobActived(event);
//				// TODO 通知任务已被通知
//			}
//		}

	}
}
