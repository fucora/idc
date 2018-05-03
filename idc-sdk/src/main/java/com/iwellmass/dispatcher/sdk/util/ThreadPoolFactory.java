package com.iwellmass.dispatcher.sdk.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务执行线程池的工厂类
 * @author Ming.Li
 *
 */
public class ThreadPoolFactory {
	
	/**
	 * 生成最大线程数为5的线程池，每个任务类对应独立的线程池
	 * @param threadFactory
	 * @return
	 */
	public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
		return new ThreadPoolExecutor(1, 5, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);
    }
}
