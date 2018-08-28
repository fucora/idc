package com.iwellmass.idc.service;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

public class ComplementRequest {

	@ApiModelProperty("任务ID")
	private String taskId;

	@ApiModelProperty("任务组")
	private String group;

	@ApiModelProperty("开始时间，yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd")
	private LocalDate startTime;

	@ApiModelProperty("截至时间，yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd")
	private LocalDate endTime;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public LocalDate getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDate startTime) {
		this.startTime = startTime;
	}

	public LocalDate getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDate endTime) {
		this.endTime = endTime;
	}

}
