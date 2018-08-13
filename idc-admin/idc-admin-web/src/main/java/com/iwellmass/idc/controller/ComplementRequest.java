package com.iwellmass.idc.controller;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

public class ComplementRequest {

	@ApiModelProperty("任务ID")
	private Integer jobId;

	@ApiModelProperty("开始时间，yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd")
	private LocalDate startTime;

	@ApiModelProperty("截至时间，yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd")
	private LocalDate endTime;

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
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
