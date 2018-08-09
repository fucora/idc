package com.iwellmass.idc.model;

import java.sql.Timestamp;

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

	@ApiModelProperty("业务日期始， yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	private Timestamp loadDateFrom;

	@ApiModelProperty("业务日期止， yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	private Timestamp loadDateTo;

	@ApiModelProperty("运行时间始， yyyy-MM-dd hh:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private Timestamp executeTimeFrom;
	
	@ApiModelProperty("运行时间止， yyyy-MM-dd hh:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private Timestamp executeTimeTo;
	

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

	public Timestamp getExecuteTimeFrom() {
		return executeTimeFrom;
	}

	public void setExecuteTimeFrom(Timestamp executeTimeFrom) {
		this.executeTimeFrom = executeTimeFrom;
	}

	public Timestamp getExecuteTimeTo() {
		return executeTimeTo;
	}

	public void setExecuteTimeTo(Timestamp executeTimeTo) {
		this.executeTimeTo = executeTimeTo;
	}

}
