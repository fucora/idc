package com.iwellmass.idc.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

/**
 * 任务实例
 */
@Entity
@IdClass(JobInstancePK.class)
@Table(name = "t_idc_job_instance")
public class JobInstance {

	private Integer instanceId;

	private String taskId;

	private String groupId;

	private LocalDateTime loadDate;

	private TaskType taskType;

	private JobInstanceStatus status;

	private String assignee;

	private String parameters;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private JobInstanceType type;

	@ApiModelProperty("执行ID")
	@Column(name = "id")
	public Integer getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Integer instanceId) {
		this.instanceId = instanceId;
	}

	@ApiModelProperty("任务 ID")
	@Id
	@Column(name = "task_id")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Id
	@Column(name = "groupId", length = 50)
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@ApiModelProperty("业务日期")
	@Id
	@Column(name = "load_date")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	public LocalDateTime getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(LocalDateTime loadDate) {
		this.loadDate = loadDate;
	}

	@Column(name = "task_type")
	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	@ApiModelProperty("参数设置")
	@Column(name = "parameters", length = 2000)
	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	@ApiModelProperty("责任人")
	@Column(name = "assignee")
	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	@ApiModelProperty("开始时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "start_time")
	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	@ApiModelProperty("结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "end_time")
	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	@ApiModelProperty("任务实例状态")
	@Column(name = "status")
	public JobInstanceStatus getStatus() {
		return status;
	}

	public void setStatus(JobInstanceStatus status) {
		this.status = status;
	}

	@ApiModelProperty("实例类型")
	@Column(name = "type")
	public JobInstanceType getType() {
		return type;
	}

	public void setType(JobInstanceType type) {
		this.type = type;
	}
}
