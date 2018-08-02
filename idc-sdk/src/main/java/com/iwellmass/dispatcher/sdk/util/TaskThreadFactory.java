package com.iwellmass.dispatcher.sdk.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class TaskThreadFactory implements ThreadFactory {
	
	private String threadName;
	
	private AtomicLong idx = new AtomicLong(1);
	
	public TaskThreadFactory(String poolName) {
		this.threadName = poolName;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, threadName + "-" + idx.getAndIncrement());
		return thread;
	}

}
