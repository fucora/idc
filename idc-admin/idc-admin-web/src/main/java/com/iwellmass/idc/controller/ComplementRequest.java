package com.iwellmass.idc.controller;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

public class ComplementRequest {
	
	@ApiModelProperty("任务ID")
	private Integer jobId;

	@ApiModelProperty("开始时间，yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd")
	private LocalDate start;
	
	@ApiModelProperty("截至时间，yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd")
	private LocalDate end;
	
	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public LocalDate getStart() {
		return start;
	}

	public void setStart(LocalDate start) {
		this.start = start;
	}

	public LocalDate getEnd() {
		return end;
	}

	public void setEnd(LocalDate end) {
		this.end = end;
	}

}
