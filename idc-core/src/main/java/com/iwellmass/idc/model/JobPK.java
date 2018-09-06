package com.iwellmass.idc.model;

import java.io.Serializable;

public class JobPK implements Serializable {

	private static final long serialVersionUID = -6687330393375678068L;

	private String taskId;

	private String groupId;

	public JobPK() {
	}

	public JobPK(String taskId, String groupId) {
		super();
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

	public void setGroupId(String group) {
		this.groupId = group;
	}

	@Override
	public String toString() {
		return "JobPK [taskId=" + taskId + ", groupId=" + groupId + "]";
	}

}
