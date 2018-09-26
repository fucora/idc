package com.iwellmass.idc.model;

import java.io.Serializable;

public class JobDependencyPK implements Serializable {

	private static final long serialVersionUID = -4479061532661305224L;

	private String srcTaskId;

	private String srcGroupId;

	private String taskId;

	private String groupId;

	public String getSrcTaskId() {
		return srcTaskId;
	}

	public void setSrcTaskId(String srcTaskId) {
		this.srcTaskId = srcTaskId;
	}

	public String getSrcGroupId() {
		return srcGroupId;
	}

	public void setSrcGroupId(String srcGroupId) {
		this.srcGroupId = srcGroupId;
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
