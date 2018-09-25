package com.iwellmass.idc.executor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class StartEvent implements IDCJobEvent {

	private static final long serialVersionUID = 4050183906735764215L;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime startTime;
	
	private Integer instanceId;
	private String message;
	
	public StartEvent() {
		this.startTime = LocalDateTime.now();
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public StartEvent setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
		return this;
	}

	public Integer getInstanceId() {
		return instanceId;
	}

	public StartEvent setInstanceId(Integer instanceId) {
		this.instanceId = instanceId;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public StartEvent setMessage(String message) {
		this.message = message;
		return this;
	}

}
