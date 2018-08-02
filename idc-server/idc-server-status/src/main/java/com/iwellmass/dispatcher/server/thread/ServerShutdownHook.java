package com.iwellmass.dispatcher.server.thread;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.server.thrift.impl.StatusServerServiceImpl;

/**
 * 结束进程前的清理工作
 * @author duheng
 *
 */
public class ServerShutdownHook implements Runnable {
	
	private final static Logger logger = LoggerFactory.getLogger(ServerShutdownHook.class);

	private Scheduler scheduler;
	
	private StatusServerServiceImpl statusServerService;
	
	public ServerShutdownHook(Scheduler scheduler, StatusServerServiceImpl statusServerService) {
		this.scheduler = scheduler;
		this.statusServerService = statusServerService;
	}

	@Override
	public void run() {

		logger.info("调度服务器开始停止...");
		try {
			if(scheduler != null) {
				scheduler.shutdown(true);				
			}
		} catch (SchedulerException e) {
			logger.error("停止Scheduler出错，错误信息：{}", e);
		}
		
		logger.info("状态服务器开始停止...");
		statusServerService.shutdown();
		
		try {
			Thread.sleep(Constants.TIME_OUT * 2);
		} catch (InterruptedException e) {
		}
		logger.info("服务器停止完成...");
	}

}
