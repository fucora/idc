package com.iwellmass.dispatcher.common.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.iwellmass.dispatcher.common.thread.DealAlarmThread;

/**
 * 提供监控预警相关功能
 * @author duheng
 *
 */
public class AlarmUtils {

	private static ExecutorService executor;

	/**
	 * 通过线程池处理预警
	 */
	public static void sendAndRecordAlarm(String alarmKey, Integer taskId, String remarks) {
		if(executor == null) {
			executor = Executors.newFixedThreadPool(10);
		}
		executor.execute(new DealAlarmThread(taskId, alarmKey, remarks));
	}
}
