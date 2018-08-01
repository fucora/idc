package com.iwellmass.idc.model;

import java.sql.Timestamp;

import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;

/**
 * 任务实例
 */
//@Entity
//@Table(name = "t_idc_job_instance")
public class JobInstance {

	// 实例类型
	private Integer id;

	private Integer jobId;

	private String jobName;

	private String taskType;

	private String assignee;

	private Timestamp loadDate;

	private Timestamp startTime;

	private Timestamp endTime;

	@Id
	@ApiModelProperty("执行ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ApiModelProperty("JOB ID")
	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	@ApiModelProperty("JOB 名称")
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@ApiModelProperty("任务类型")
	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	@ApiModelProperty("责任人")
	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	@ApiModelProperty("责任人")
	public Timestamp getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(Timestamp loadDate) {
		this.loadDate = loadDate;
	}

	@ApiModelProperty("开始时间")
	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	@ApiModelProperty("结束时间")
	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

}
