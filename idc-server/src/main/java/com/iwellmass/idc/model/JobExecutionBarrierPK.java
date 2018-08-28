package com.iwellmass.idc.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class JobExecutionBarrierPK implements Serializable {

	private static final long serialVersionUID = 7546711538636604222L;

	private int instanceId;

	private String taskId;

	private String gorupId;

	private LocalDateTime loadDate;

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getGorupId() {
		return gorupId;
	}

	public void setGorupId(String gorupId) {
		this.gorupId = gorupId;
	}

	public LocalDateTime getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(LocalDateTime loadDate) {
		this.loadDate = loadDate;
	}

}
