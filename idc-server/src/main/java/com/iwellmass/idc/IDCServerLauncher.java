package com.iwellmass.idc;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * 使用 spring-cloud 作为我们的
 * 
 * @since 2.0
 */
@SpringCloudApplication
@EnableEurekaClient
@EnableFeignClients
public class IDCServerLauncher {
	
	public static void main(String[] args) {
		SpringApplication.run(IDCServerLauncher.class, args);
	}

	
//	ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
//			"classpath:spring/applicationContext-*.xml");
//	applicationContext.start();
//
//	// 设置Context全局变量
//	SpringContext.setApplicationContext(applicationContext);
//
//	try {
//		SchedulingEngine engine = applicationContext.getBean(SchedulingEngine.class);
//		engine.start();
//	} catch (Exception e) {
//		LOGGER.error("启动流程引擎出错，错误信息{}", e);
//		System.exit(-1);
//	}
//
//	try {
//		ServerStarter starter = applicationContext.getBean(ServerStarter.class);
//		starter.start();
//	} catch (Exception e) {
//		LOGGER.error("启动调度状态服务出错，错误信息{}", e);
//		System.exit(-1);
//	}
}
