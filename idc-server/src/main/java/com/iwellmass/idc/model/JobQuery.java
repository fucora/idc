package com.iwellmass.idc.model;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.model.ContentType;
import com.iwellmass.idc.model.JobInstanceType;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.TaskType;

import io.swagger.annotations.ApiModelProperty;

public class JobQuery {

	@ApiModelProperty("任务名")
	private String taskName;

	@ApiModelProperty("任务类型")
	private ContentType contentType;

	@ApiModelProperty("调度类型类型")
	private ScheduleType scheduleType;
	
	@ApiModelProperty("实例类型")
	private JobInstanceType instanceType;

	@ApiModelProperty("节点类型")
	private List<TaskType> taskTypes;

	@ApiModelProperty("负责人")
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

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
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

	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}
}
