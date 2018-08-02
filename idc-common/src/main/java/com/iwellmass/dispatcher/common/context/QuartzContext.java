package com.iwellmass.dispatcher.common.context;

import org.quartz.Scheduler;

/**
 * 保存quartz的上下文，在启动的时候加载进来
 * 
 * @author duheng
 *
 */
public class QuartzContext {

	// 设置为全局变量，后续再quartz任务中使用
	private static volatile Scheduler scheduler;

	public static Scheduler getScheduler() {
		return scheduler;
	}

	public static void setScheduler(Scheduler scheduler) {
		QuartzContext.scheduler = scheduler;
	}

}
