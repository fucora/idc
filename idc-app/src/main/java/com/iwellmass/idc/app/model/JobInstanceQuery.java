package com.iwellmass.idc.app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.common.criteria.Between;
import com.iwellmass.common.criteria.Equal;
import com.iwellmass.common.criteria.In;
import com.iwellmass.common.criteria.Like;
import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.idc.model.JobInstanceType;
import com.iwellmass.idc.model.TaskType;

import io.swagger.annotations.ApiModelProperty;

public class JobInstanceQuery implements SpecificationBuilder{

	@ApiModelProperty("任务名")
	@Like
	private String taskName;

	@ApiModelProperty("节点类型")
	@In("taskType")
	private List<TaskType> taskTypes;
	
	@ApiModelProperty("任务类型")
	@Equal
	private String contentType;
	
	@ApiModelProperty("实例类型")
	@Equal("type")
	private JobInstanceType instanceType;

	@ApiModelProperty("负责人")
	@Equal
	private String assignee;

	@ApiModelProperty("业务日期始， yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	@Between(value = "loadDate", to = "loadDateTo")
	private LocalDate loadDateFrom;

	@ApiModelProperty("业务日期止， yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	private LocalDate loadDateTo;

	@ApiModelProperty("运行时间始， yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	@Between(value = "startTime", to = "executeTimeTo")
	private LocalDateTime executeTimeFrom;

	@ApiModelProperty("运行时间止， yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private LocalDateTime executeTimeTo;

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

	public List<TaskType> getTaskTypes() {
		return taskTypes;
	}

	public void setTaskTypes(List<TaskType> taskTypes) {
		this.taskTypes = taskTypes;
	}

	public LocalDate getLoadDateFrom() {
		return loadDateFrom;
	}

	public void setLoadDateFrom(LocalDate loadDateFrom) {
		this.loadDateFrom = loadDateFrom;
	}

	public LocalDate getLoadDateTo() {
		return loadDateTo;
	}

	public void setLoadDateTo(LocalDate loadDateTo) {
		this.loadDateTo = loadDateTo;
	}

	public LocalDateTime getExecuteTimeFrom() {
		return executeTimeFrom;
	}

	public void setExecuteTimeFrom(LocalDateTime executeTimeFrom) {
		this.executeTimeFrom = executeTimeFrom;
	}

	public LocalDateTime getExecuteTimeTo() {
		return executeTimeTo;
	}

	public void setExecuteTimeTo(LocalDateTime executeTimeTo) {
		this.executeTimeTo = executeTimeTo;
	}

	public JobInstanceType getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(JobInstanceType instanceType) {
		this.instanceType = instanceType;
	}
	
}
