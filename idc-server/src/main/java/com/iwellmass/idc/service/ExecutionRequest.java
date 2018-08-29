package com.iwellmass.idc.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExecutionRequest {

	private Integer instanceId;
	
	private String taskId;

	private String group;
	
	private LocalDateTime loadDate;

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

	public Integer getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Integer instanceId) {
		this.instanceId = instanceId;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	
	public LocalDateTime getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(LocalDateTime loadDate) {
		this.loadDate = loadDate;
	}

	@Override
	public String toString() {
		return "ExecutionRequest [taskId=" + taskId + ", group=" + group + ", loadDate=" + loadDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "]";
	}
}
