package com.iwellmass.idc.service;

public class ExecutionRequest {

	private String taskId;

	private String groupId;

	private String jobParameter;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getJobParameter() {
		return jobParameter;
	}

	public void setJobParameter(String jobParameter) {
		this.jobParameter = jobParameter;
	}

}
