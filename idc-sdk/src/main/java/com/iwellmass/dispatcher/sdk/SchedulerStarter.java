package com.iwellmass.dispatcher.sdk;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.server.TServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.sdk.model.DDCException;
import com.iwellmass.dispatcher.sdk.model.ExecutingTaskInfo;
import com.iwellmass.dispatcher.sdk.service.ITaskService;
import com.iwellmass.dispatcher.sdk.thread.HeartBeatThread;
import com.iwellmass.dispatcher.sdk.thread.JVMShutdownHook;
import com.iwellmass.dispatcher.sdk.thread.ServerListThread;
import com.iwellmass.dispatcher.sdk.thread.StatusReportThread;
import com.iwellmass.dispatcher.sdk.thread.ThriftServerThread;
import com.iwellmass.dispatcher.sdk.thrift.AgentThriftService;
import com.iwellmass.dispatcher.sdk.thrift.impl.AgentServiceImpl;
import com.iwellmass.dispatcher.sdk.util.ApplicationContext;
import com.iwellmass.dispatcher.sdk.util.StringUtils;
import com.iwellmass.dispatcher.sdk.util.TaskThreadFactory;
import com.iwellmass.dispatcher.thrift.model.ExecutorRegisterResult;
import com.iwellmass.dispatcher.thrift.sdk.AgentService;
import com.iwellmass.idc.lookup.SourceLookup;

public class SchedulerStarter {

	private final static Logger log = LoggerFactory.getLogger(SchedulerStarter.class);

	//项目编号
	private String appKey;

	//任务列表
	private Set<ITaskService> tasks;

	public SchedulerStarter(String appKey, Set<ITaskService> tasks, String serverUrl) {
		this.appKey = appKey;
		this.tasks = tasks;

		Map<String, ITaskService> taskMap = new HashMap<String, ITaskService>();
		for(ITaskService task : tasks) {
			taskMap.put(task.getClass().getName(), task);
		}
		ApplicationContext.setAppkey(appKey);
		ApplicationContext.setTaskMap(taskMap);
		ApplicationContext.setServerUrl(serverUrl);
	}
	
	public SchedulerStarter withSourceLookup(String className, SourceLookup lookup) {
		ApplicationContext.getSourceLookupMap().put(className, lookup);
		return this;
	}

	/**
	 * 启动任务执行Thrift客户端
	 * 注册应用实例
	 * 启动心跳线程
	 * @throws DDCException 
	 */
	public void start() throws DDCException {

		if (StringUtils.isBlank(appKey)) {
			log.error("DDC-项目编号不能为空！");
			throw new DDCException("DDC-项目编号不能为空！");
		}

		if (tasks == null || tasks.size()==0) {
			log.error("DDC-任务不能为空！");
			throw new DDCException("DDC-任务不能为空！");
		}

		//当前正在执行的任务列表
		final CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList = new CopyOnWriteArrayList<ExecutingTaskInfo>();

		//接收调度中心任务执行请求的thrift服务实现
		AgentService.Iface agentService = new AgentServiceImpl(executingTaskList);

		AgentThriftService agentThriftService = new AgentThriftService(agentService);
		final TServer server = agentThriftService.initThriftServer();
		if(server == null) {
			log.error("DDC-注册任务执行器Thrift服务失败！");
			throw new DDCException("DDC-注册任务执行器Thrift服务失败！");
		}
		ApplicationContext.setPort(agentThriftService.getPort());		

		final ThriftServerThread thriftService = new ThriftServerThread(server);
		new Thread(thriftService, "DDC-ThriftServerThread").start();

		if(!thriftService.isServerServing()) {
			log.error("DDC-启动Thrift服务失败！");
			throw new DDCException("DDC-启动Thrift服务失败！");
		}

		//注册当前应用，内部实现了重试
		ExecutorRegisterResult result = agentThriftService.registerExecutor(appKey, ApplicationContext.getTaskMap());
		if(result==null || !result.isSucceed()) {
			log.error("DDC-注册任务执行客户端失败！appKey={}, 错误信息：{}", appKey, result==null?null:result.getMessage());
			throw new DDCException("DDC-注册任务执行客户端失败！appKey={%s}, 错误信息：{%s}", appKey, result==null?null:result.getMessage());
		}
		// 注册当前 SourceLookup，复用上面逻辑
		Map<String, SourceLookup> sourceLookupMap = ApplicationContext.getSourceLookupMap();
		if (!(sourceLookupMap == null || sourceLookupMap.isEmpty())) {
			ExecutorRegisterResult sourceLookupResult = agentThriftService.registerSourceLookup("SourceLookup", sourceLookupMap);
			if(sourceLookupResult==null || !sourceLookupResult.isSucceed()) {
				log.error("DDC-注册数据发现客户端失败！appKey={}, 错误信息：{}", appKey, result==null?null:result.getMessage());
				throw new DDCException("DDC-注册数据发现执行客户端失败！appKey={%s}, 错误信息：{%s}", appKey, result==null?null:result.getMessage());
			}
		}
		ApplicationContext.setAppId(result.getAppId());

		//管理心跳线程和更新状态服务地址列表线程
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3, new TaskThreadFactory("DDC-ScheduledThreadPool-Thread"));

		HeartBeatThread heartBeatThread = new HeartBeatThread(executingTaskList);
		scheduledExecutorService.scheduleAtFixedRate(heartBeatThread, 13, 15, TimeUnit.SECONDS);
		
		StatusReportThread statusReportThread = new StatusReportThread(executingTaskList);
		scheduledExecutorService.scheduleAtFixedRate(statusReportThread, 3, 5, TimeUnit.SECONDS);

		ServerListThread serverListThread = new ServerListThread();
		scheduledExecutorService.scheduleAtFixedRate(serverListThread, 1, 5, TimeUnit.MINUTES);

		Runtime.getRuntime().addShutdownHook(new Thread(new JVMShutdownHook(scheduledExecutorService, thriftService, server, executingTaskList)));
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public Set<ITaskService> getTasks() {
		return tasks;
	}

	public void setTasks(Set<ITaskService> tasks) {
		this.tasks = tasks;
	}

}
