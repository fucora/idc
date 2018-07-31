package com.iwellmass.idc.lookup;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceLookupManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SourceLookupManager.class);

	private ScheduledExecutorService schExecutor = Executors.newSingleThreadScheduledExecutor();

	public void schedule(SourceLookup sourceLookup) {
		LookupTask task = new LookupTask();
		task.lookup = sourceLookup;
		schedule0(task);
	}
	
	private void schedule0(LookupTask task) {
		schExecutor.schedule(task, task.lookup.getInterval(), TimeUnit.MILLISECONDS);
	}

	class LookupTask implements Runnable {

		private SourceLookup lookup;

		@Override
		public void run() {
			// 停止检测
			if( lookup.isHalt()) {
				LOGGER.info("停止检测进程 {} ", lookup);
			}
			
			try {
				LookupContextImpl ctx = new LookupContextImpl();
				lookup.lookup(ctx);
			} catch (Throwable e) {
				LOGGER.error("检测失败, ERROR: {}", e.getMessage(), e);
			} finally {
				if (!lookup.isHalt()) {
					schedule0(this);
				}
			}
		}
	}
}
