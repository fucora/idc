package com.iwellmass.dispatcher.sdk.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import com.iwellmass.dispatcher.sdk.service.ITaskService;
import com.iwellmass.idc.lookup.SourceLookup;

public class ApplicationContext {
	
	private static String appkey;
	
	private static volatile int appId;
	
	private static String nodeCode = NetUtils.NODE_CODE;
	
	private static Map<String, ITaskService> taskMap;
	
	private static Map<String, SourceLookup> sourceLookupMap;
	
	private static String serverUrl = "ss.ddc.dmall.com";
	
	private static String ip = NetUtils.CURRENT_HOST_IP;
	
	private static int port;
	
	private static volatile Map<Integer, ExecutorService> executorServiceMap = new ConcurrentHashMap<Integer, ExecutorService>();

	public static String getAppkey() {
		return appkey;
	}

	public static void setAppkey(String appkey) {
		ApplicationContext.appkey = appkey;
	}

	public static int getAppId() {
		return appId;
	}

	public static void setAppId(int appId) {
		ApplicationContext.appId = appId;
	}

	public static String getNodeCode() {
		return nodeCode;
	}

	public static Map<String, ITaskService> getTaskMap() {
		return taskMap;
	}

	public static void setTaskMap(Map<String, ITaskService> taskMap) {
		ApplicationContext.taskMap = taskMap;
	}

	public static String getServerUrl() {
		return serverUrl;
	}

	public static void setServerUrl(String serverUrl) {
		if(!StringUtils.isBlank(serverUrl)) {
			ApplicationContext.serverUrl = serverUrl;			
		}
	}

	public static String getIp() {
		return ip;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		ApplicationContext.port = port;
	}
	
	/**
	 * 获取任务执行的线程池
	 * 每个任务对应独立的线程池，若线程池已经被创建则沿用，若没有则新创建
	 * @param beanName
	 * @param taskId
	 * @return
	 */
	public static ExecutorService getExecutorService(String beanName, int taskId) {
			
		ExecutorService es = executorServiceMap.get(taskId);
		if(es == null) {
			synchronized(ApplicationContext.class) {
				es = executorServiceMap.get(taskId);
				if(es == null) {
					String threadName = beanName;
					if(beanName.indexOf(".") > 0) {
						threadName = beanName.substring(beanName.lastIndexOf(".") + 1) + "-" + taskId;
					}
					es = ThreadPoolFactory.newCachedThreadPool(new TaskThreadFactory(threadName + "-TaskThread"));
					executorServiceMap.put(taskId, es);
				}
			}
		}
		return es;
	}
	
	/**
	 * 返回当前所有的任务执行线程池，shutdownhook调用
	 * @return
	 */
	public static Collection<ExecutorService> getAllExecutorServices() {
		return executorServiceMap.values();
	}

	public static Map<String, SourceLookup> getSourceLookupMap() {
		if (sourceLookupMap == null) {
			synchronized (ApplicationContext.class) {
				if (sourceLookupMap == null) {
					sourceLookupMap = new HashMap<>();
				}
			}
		}
		return sourceLookupMap;
	}

}
