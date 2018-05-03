package com.iwellmass.dispatcher.sdk.thread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import org.apache.thrift.server.TServer;

import com.iwellmass.dispatcher.sdk.base.StatusProcessor;
import com.iwellmass.dispatcher.sdk.model.ExecutingTaskInfo;
import com.iwellmass.dispatcher.sdk.util.ApplicationContext;
import com.iwellmass.dispatcher.sdk.util.ExecuteContext;
import com.iwellmass.dispatcher.thrift.bvo.TaskStatus;
import com.iwellmass.dispatcher.thrift.model.ExecuteStatus;
import com.iwellmass.dispatcher.thrift.model.TaskStatusInfo;

/**
 * 应用停止时的操作，使用kill -9将不会生效
 * @author Ming.Li
 *
 */
public class JVMShutdownHook extends StatusProcessor implements Runnable {
	
	private ExecutorService executorService;
	
	private ThriftServerThread thriftService;
	
	private TServer server;
	
	private CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList;

	public JVMShutdownHook(ExecutorService executorService, ThriftServerThread thriftService, TServer server, CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList) {
		this.executorService = executorService;
		this.thriftService = thriftService;
		this.server = server;
		this.executingTaskList = executingTaskList;
	}
	
	@Override
	public void run() {
		
		//停止接收任务的Thrift服务
		server.stop();
		
		//处理正在执行的任务
		int idx = 0;
		while(executingTaskList.size() > 0 && idx < 10) {
			idx ++;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		if(executingTaskList.size() > 0) {
			List<TaskStatusInfo> killedList = new ArrayList<TaskStatusInfo>();
			for(ExecutingTaskInfo execute : executingTaskList) {
				TaskStatusInfo status = generateTaskStatusInfo(execute.getTaskEntity());
				
				ExecuteStatus es = new ExecuteStatus();
				es.setStatus(TaskStatus.JVM_STOPED);
				es.setTime(System.currentTimeMillis());
				
				List<ExecuteStatus> currentStatus = ((ExecuteContext)execute.getExecuteContext()).pollExecuteStatus();
				status.setStatusList(currentStatus);
				status.addToStatusList(es);
				killedList.add(status);
			}
			if(killedList.size() > 0) {
				sendTaskStatusList(killedList);
			}
		}
		
		//关闭线程池
		executorService.shutdown();
		
		//关闭任务执行线程池
		Collection<ExecutorService> taskExecutorService = ApplicationContext.getAllExecutorServices();
		for(ExecutorService es : taskExecutorService) {
			es.shutdownNow();
		}
	}

	public ThriftServerThread getThriftService() {
		return thriftService;
	}

	public void setThriftService(ThriftServerThread thriftService) {
		this.thriftService = thriftService;
	}

	public TServer getServer() {
		return server;
	}

	public void setServer(TServer server) {
		this.server = server;
	}

	public CopyOnWriteArrayList<ExecutingTaskInfo> getExecutingTaskList() {
		return executingTaskList;
	}

	public void setExecutingTaskList(CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList) {
		this.executingTaskList = executingTaskList;
	}

}
