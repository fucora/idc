package com.iwellmass.idc.scheduler.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class TaskID implements Serializable{
	
	private static final long serialVersionUID = -7574873242259636335L;

	private String name;
	
	private String group;
	
	public TaskID() {}
	
	public TaskID(String name) {
		this.name = name;
		this.group = Task.GROUP_PRIMARY;
	}
}
