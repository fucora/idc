package com.iwellmass.dispatcher.common.context;

import org.springframework.context.ApplicationContext;

/**
 * 保存spring的上下文，在启动的时候加载进来
 * @author duheng
 *
 */
public class SpringContext {

	//设置为全局变量，后续再quartz任务中使用
	private static volatile ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringContext.applicationContext = applicationContext;
	}
	
	
}
