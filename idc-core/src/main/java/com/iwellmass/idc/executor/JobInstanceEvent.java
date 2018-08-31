package com.iwellmass.idc.executor;

import java.io.Serializable;

public class JobInstanceEvent implements Serializable{

	private static final long serialVersionUID = 6207313078646929091L;

	private Integer instanceId;

	private String message;

	public Integer getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Integer instanceId) {
		this.instanceId = instanceId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
