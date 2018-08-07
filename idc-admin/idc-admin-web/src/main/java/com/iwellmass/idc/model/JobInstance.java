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

	private String taskName;

	private String contentType;

	private String status;

	private String assignee;

	private Timestamp excuteTime;

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
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@ApiModelProperty("任务类型")
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@ApiModelProperty("责任人")
	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	@ApiModelProperty("业务时间")
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

	@ApiModelProperty("运行时间")
	public Timestamp getExcuteTime() {
		return excuteTime;
	}

	public void setExcuteTime(Timestamp excuteTime) {
		this.excuteTime = excuteTime;
	}

	@ApiModelProperty("状态")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
