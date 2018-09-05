package com.iwellmass.idc.executor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class StartEvent implements IDCJobEvent {

	private static final long serialVersionUID = 4050183906735764215L;

	private LocalDateTime startTime;
	private Integer instanceId;
	private String message;

	@JsonFormat(pattern = "yyyyMMddHHmmss", timezone = "GMT+8")
	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
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
