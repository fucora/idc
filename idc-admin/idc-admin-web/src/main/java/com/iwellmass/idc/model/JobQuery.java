package com.iwellmass.idc.model;

import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

public class JobQuery {

	@ApiModelProperty("任务名")
	private String taskName;

	@ApiModelProperty("任务类型")
	private String contentType;

	@ApiModelProperty("实例类型")
	private Integer type;

	@ApiModelProperty("负责人")
	private String assignee;

	@ApiModelProperty("业务时期")
	private Timestamp loadTime;

	@ApiModelProperty("业务日期始， yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd")
	private Timestamp loadDateFrom;

	@ApiModelProperty("业务时期止， yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd")
	private Timestamp loadDateTo;

	@ApiModelProperty("运行时间始， yyyy-MM-dd hh:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private Timestamp excuteTimeFrom;
	
	@ApiModelProperty("运行时间止， yyyy-MM-dd hh:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private Timestamp excuteTimeTo;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public Date getLoadTime() {
		return loadTime;
	}

	public void setLoadTime(Timestamp loadTime) {
		this.loadTime = loadTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Timestamp getLoadDateFrom() {
		return loadDateFrom;
	}

	public void setLoadDateFrom(Timestamp loadDateFrom) {
		this.loadDateFrom = loadDateFrom;
	}

	public Timestamp getLoadDateTo() {
		return loadDateTo;
	}

	public void setLoadDateTo(Timestamp loadDateTo) {
		this.loadDateTo = loadDateTo;
	}

	public Timestamp getExcuteTimeFrom() {
		return excuteTimeFrom;
	}

	public void setExcuteTimeFrom(Timestamp excuteTimeFrom) {
		this.excuteTimeFrom = excuteTimeFrom;
	}

	public Timestamp getExcuteTimeTo() {
		return excuteTimeTo;
	}

	public void setExcuteTimeTo(Timestamp excuteTimeTo) {
		this.excuteTimeTo = excuteTimeTo;
	}
	
}
