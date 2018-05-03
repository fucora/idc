package com.iwellmass.dispatcher.sdk.model;

import com.iwellmass.dispatcher.sdk.service.ITaskService;
import com.iwellmass.dispatcher.sdk.util.IBasicExecuteContext;
import com.iwellmass.dispatcher.thrift.model.TaskEntity;

/**
 * 正在执行的任务对象
 * @author Ming.Li
 *
 */
public class ExecutingTaskInfo {
	
	//task对象
	private TaskEntity taskEntity;
	
	//执行类对象
	private ITaskService taskService;
	
	//执行上下文
	private IBasicExecuteContext executeContext;
	
	//执行线程ID，若还未被线程执行则为null
	private Long threadId;

	public TaskEntity getTaskEntity() {
		return taskEntity;
	}

	public void setTaskEntity(TaskEntity taskEntity) {
		this.taskEntity = taskEntity;
	}

	public ITaskService getTaskService() {
		return taskService;
	}

	public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}

	public IBasicExecuteContext getExecuteContext() {
		return executeContext;
	}

	public void setExecuteContext(IBasicExecuteContext executeContext) {
		this.executeContext = executeContext;
	}

	public Long getThreadId() {
		return threadId;
	}

	public void setThreadId(Long threadId) {
		this.threadId = threadId;
	}	
}
