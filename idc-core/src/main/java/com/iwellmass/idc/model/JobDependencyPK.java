package com.iwellmass.idc.model;

import java.io.Serializable;

public class JobDependencyPK implements Serializable {

	private static final long serialVersionUID = -4479061532661305224L;
	
	private String taskId;

	private String groupId;

	private String dependencyTaskId;

	private String dependencyGroupId;

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

	public String getDependencyTaskId() {
		return dependencyTaskId;
	}

	public void setDependencyTaskId(String dependencyTaskId) {
		this.dependencyTaskId = dependencyTaskId;
	}

	public String getDependencyGroupId() {
		return dependencyGroupId;
	}

	public void setDependencyGroupId(String dependencyGroupId) {
		this.dependencyGroupId = dependencyGroupId;
	}
	
}
