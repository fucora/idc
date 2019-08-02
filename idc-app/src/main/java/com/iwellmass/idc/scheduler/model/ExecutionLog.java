package com.iwellmass.idc.scheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.slf4j.helpers.MessageFormatter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "idc_execution_log")
public class ExecutionLog {

	private Long id;
	
	private String instanceId;
	
	private LocalDateTime time;
	
	private String message;

	private String detail;
	
	public ExecutionLog() {
		this.time = LocalDateTime.now();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "instance_id")
	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	@Column(name = "time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	@Column(name = "message", length = 1000)
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public static ExecutionLog createLog(String instanceId, String message, Object...args) {
		ExecutionLog log = new ExecutionLog();
		log.setInstanceId(instanceId);
		log.setMessage(message);
		if (message != null) {
			if (args == null || args.length == 0) {
				log.setMessage(message);
			} else {
				log.setMessage(MessageFormatter.arrayFormat(message, args).getMessage());
			}
		}
		log.setTime(LocalDateTime.now());
		return log;
	}

}
