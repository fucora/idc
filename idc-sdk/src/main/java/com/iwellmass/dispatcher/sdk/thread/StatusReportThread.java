package com.iwellmass.dispatcher.sdk.thread;

import java.util.concurrent.CopyOnWriteArrayList;

import com.iwellmass.dispatcher.sdk.base.StatusProcessor;
import com.iwellmass.dispatcher.sdk.model.ExecutingTaskInfo;
import com.iwellmass.dispatcher.thrift.model.TaskStatusInfo;

/**
 * 定时上报执行中任务的当前已有状态
 * 每个任务独立上报
 * @author Ming.Li
 *
 */
public class StatusReportThread extends StatusProcessor implements Runnable {

	private CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList;
	
	public StatusReportThread(CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList) {
		this.executingTaskList = executingTaskList;
	}
	
	@Override
	public void run() {

		for(ExecutingTaskInfo taskInfo : executingTaskList) {
			
			Long threadId = taskInfo.getThreadId();
			if(threadId != null) {
				TaskStatusInfo status = getTaskStatusInfo(taskInfo);
				if(status != null && status.getStatusList() != null && !status.getStatusList().isEmpty()) { //有可用状态才上报
					sendTaskStatus(status);					
				}
			}			
		}
	}

}
