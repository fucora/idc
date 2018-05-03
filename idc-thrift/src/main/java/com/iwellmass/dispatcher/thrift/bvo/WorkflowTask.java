package com.iwellmass.dispatcher.thrift.bvo;

import java.util.List;

public class WorkflowTask {
	
	//当前任务节点对应的任务编号
	private int taskId;
	
	//当前任务节点的后续任务
	private List<WorkflowTask> children;

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public List<WorkflowTask> getChildren() {
		return children;
	}

	public void setChildren(List<WorkflowTask> children) {
		this.children = children;
	}
}
