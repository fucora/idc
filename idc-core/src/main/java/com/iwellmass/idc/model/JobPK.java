package com.iwellmass.idc.model;

import java.io.Serializable;

public class JobPK implements Serializable{

	private static final long serialVersionUID = -6687330393375678068L;

	private String taskId;
	
	private String groupId;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String group) {
		this.groupId = group;
	}
	
	
}
