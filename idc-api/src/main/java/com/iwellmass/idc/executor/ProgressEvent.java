package com.iwellmass.idc.executor;

import com.iwellmass.idc.model.JobInstanceStatus;

import java.time.LocalDateTime;

import static org.slf4j.helpers.MessageFormatter.arrayFormat;

public class ProgressEvent implements IDCJobEvent {

	private static final long serialVersionUID = 8419433451011829906L;

	private Integer instanceId;
	private String message;
	private JobInstanceStatus status;
	private LocalDateTime time;

	private ProgressEvent() {}
	
	public Integer getInstanceId() {
		return instanceId;
	}

	public ProgressEvent setInstanceId(Integer instanceId) {
		this.instanceId = instanceId;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public ProgressEvent setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public ProgressEvent setMessage(String message, Object...args) {
		setMessage(arrayFormat(message, args).getMessage());
		return this;
	}

	public JobInstanceStatus getStatus() {
		return status;
	}

	public ProgressEvent setStatus(JobInstanceStatus status) {
		this.status = status;
		return this;
	}
	
	// ~~ factory-method ~~
	
	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public static ProgressEvent newEvent(Integer instanceId) {
		ProgressEvent event = new ProgressEvent();
		event.setInstanceId(instanceId);
		event.time = LocalDateTime.now();
		event.status = JobInstanceStatus.RUNNING;
		return event;
	}

	@Override
	public String toString() {
		return "ProgressEvent [instanceId=" + instanceId + ", message=" + message + "]";
	}
	
	
}
