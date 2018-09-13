package com.iwellmass.idc.app.model;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
	private JobInstanceType instanceType;

	@ApiModelProperty("负责人")
	@Equal
	private String assignee;

	@ApiModelProperty("业务日期始， yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	private Timestamp loadDateFrom;

	@ApiModelProperty("业务日期止， yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	private Timestamp loadDateTo;

	@ApiModelProperty("运行时间始， yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private Timestamp executeTimeFrom;

	@ApiModelProperty("运行时间止， yyyy-MM-dd HH:mm:ss")
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

	public List<TaskType> getTaskTypes() {
		return taskTypes;
	}

	public void setTaskTypes(List<TaskType> taskTypes) {
		this.taskTypes = taskTypes;
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

	public JobInstanceType getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(JobInstanceType instanceType) {
		this.instanceType = instanceType;
	}
	
}
