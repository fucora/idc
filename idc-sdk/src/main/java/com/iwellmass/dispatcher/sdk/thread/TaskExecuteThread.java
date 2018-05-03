package com.iwellmass.dispatcher.sdk.thread;

import java.util.List;

import com.iwellmass.dispatcher.sdk.base.StatusProcessor;
import com.iwellmass.dispatcher.sdk.model.ExecutingTaskInfo;
import com.iwellmass.dispatcher.sdk.service.ITaskService;
import com.iwellmass.dispatcher.sdk.util.ExecuteContext;
import com.iwellmass.dispatcher.thrift.bvo.TaskStatus;
import com.iwellmass.dispatcher.thrift.model.ExecuteStatus;
import com.iwellmass.dispatcher.thrift.model.TaskStatusInfo;

/**
 * 任务执行线程，执行完成后将所有没被心跳线程上报的状态一起上报
 * @author Ming.Li
 *
 */
public class TaskExecuteThread extends StatusProcessor implements Runnable {
	
	//待执行任务对象
	private ExecutingTaskInfo taskInfo;
	
	//正在执行的任务列表
	private List<ExecutingTaskInfo> executingTaskList;
	
	public TaskExecuteThread(ExecutingTaskInfo taskInfo, List<ExecutingTaskInfo> executingTaskList) {
		this.taskInfo = taskInfo;
		this.executingTaskList = executingTaskList;
	}
		
	@Override
	public void run() {
		
		taskInfo.setThreadId(Thread.currentThread().getId());
		ITaskService service = taskInfo.getTaskService();
		String params = taskInfo.getTaskEntity().getParameters();
		
		//任务开始时上报状态
		TaskStatusInfo status = generateTaskStatusInfo(taskInfo.getTaskEntity());
		status.setStatusList(((ExecuteContext)taskInfo.getExecuteContext()).pollExecuteStatus());
		
		ExecuteStatus es = new ExecuteStatus();
		es.setStatus(TaskStatus.STARTED);
		es.setTime(System.currentTimeMillis());
		es.setMessage("任务执行开始");
		status.addToStatusList(es);
		
		if(!sendTaskStatus(status)) {
			for(ExecuteStatus s : status.getStatusList()) {
				((ExecuteContext)taskInfo.getExecuteContext()).addExecuteStatus(s.getStatus(), s.getMessage());
			}		
		}
		
		service.execute(params, taskInfo.getExecuteContext());	
		executingTaskList.remove(taskInfo);

		//任务执行结束时上报状态
		TaskStatusInfo statusFinished = getTaskStatusInfo(taskInfo);
		if(statusFinished != null && statusFinished.getStatusList() != null && !statusFinished.getStatusList().isEmpty()) {
			sendTaskStatus(statusFinished);			
		}		
	}
}
