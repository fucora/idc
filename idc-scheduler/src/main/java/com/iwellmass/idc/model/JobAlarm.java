package com.iwellmass.idc.model;

import java.sql.Timestamp;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

/**
 * 任务预警
 */
public class JobAlarm {
	
	private Integer id;

	@ApiModelProperty("Job ID")
	private Integer jobId;

	@ApiModelProperty("报警的原因")
	private String cause;

	@ApiModelProperty("接收者")
	private String receivers;

	@ApiModelProperty("创建者")
	private String creator;

	@ApiModelProperty("报警时间")
	private Timestamp alarmTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer taskId) {
		this.jobId = taskId;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(Timestamp alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getReceivers() {
		return receivers;
	}

	public void setReceivers(String receivers) {
		this.receivers = receivers;
	}

}
