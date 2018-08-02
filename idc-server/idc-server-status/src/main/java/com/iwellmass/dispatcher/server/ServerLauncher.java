package com.iwellmass.dispatcher.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.iwellmass.dispatcher.common.context.SpringContext;
import com.iwellmass.dispatcher.common.dag.SchedulingEngine;

/**
 * 启动类
 * @author Ming.Li
 *
 */
public class ServerLauncher {

	private final static Logger logger = LoggerFactory.getLogger(ServerLauncher.class);
	
	public static void main(String[] args) {

		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
        applicationContext.start();
        
        //设置Context全局变量
        SpringContext.setApplicationContext(applicationContext);	
        
        try {
        	SchedulingEngine engine = applicationContext.getBean(SchedulingEngine.class);
        	engine.start();
        } catch (Exception e) {
			logger.error("启动流程引擎出错，错误信息{}", e);
			System.exit(-1);
		}
        
        try {
        	ServerStarter starter = applicationContext.getBean(ServerStarter.class);        
        	starter.start();
        } catch (Exception e) {
			logger.error("启动调度状态服务出错，错误信息{}", e);
			System.exit(-1);
		}
        
	}

}
