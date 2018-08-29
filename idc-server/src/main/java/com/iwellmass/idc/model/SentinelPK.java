package com.iwellmass.idc.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SentinelPK implements Serializable {

	private static final long serialVersionUID = 7546711538636604222L;

	private String taskId;

	private String groupId;

	private LocalDateTime loadDate;

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

	public LocalDateTime getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(LocalDateTime loadDate) {
		this.loadDate = loadDate;
	}

}
