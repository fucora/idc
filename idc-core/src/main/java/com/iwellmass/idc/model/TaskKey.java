package com.iwellmass.idc.model;

import java.io.Serializable;

public class TaskKey implements Serializable{

	private static final long serialVersionUID = -5528099807403193969L;

	private String taskId;
	
	private String groupId;
	
	public TaskKey() {}
	
	public TaskKey(String taskId, String groupId) {
		this.taskId = taskId;
		this.groupId = groupId;
	}

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
	
}
