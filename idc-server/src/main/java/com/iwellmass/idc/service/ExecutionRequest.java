package com.iwellmass.idc.service;

public class ExecutionRequest {

	private String instanceId;
	
	private String taskId;

	private String group;

	private String parameters;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String groupId) {
		this.group = groupId;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return "ExecutionRequest [taskId=" + taskId + ", groupId=" + group + "]";
	}
}
