package com.iwellmass.idc.scheduler.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class TaskID implements Serializable{
	
	private static final long serialVersionUID = -7574873242259636335L;

	private String taskName;
	
	private String taskGroup;
	
	public TaskID() {}
	
	public TaskID(String name) {
		this.taskName = name;
		this.taskGroup = Task.GROUP_PRIMARY;
	}
}
