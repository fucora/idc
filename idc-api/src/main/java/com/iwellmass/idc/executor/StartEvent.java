package com.iwellmass.idc.executor;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class StartEvent implements IDCJobEvent {

	private static final long serialVersionUID = 4050183906735764215L;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime startTime;
	
	private String instanceId;
	private String message;
	
	private StartEvent() {}
	
	public LocalDateTime getStartTime() {
		return startTime;
	}

	public StartEvent setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
		return this;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public StartEvent setInstanceId(String instanceId) {
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

	
	public static StartEvent newEvent(String instanceId) {
		StartEvent event = new StartEvent();
		event.setInstanceId(instanceId);
		event.startTime = LocalDateTime.now();
		return event;
	}
}
