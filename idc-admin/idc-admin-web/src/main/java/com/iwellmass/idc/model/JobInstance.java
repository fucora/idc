package com.iwellmass.idc.model;

import java.sql.Timestamp;
import java.util.TimeZone;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

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

	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private Timestamp executeTime;

	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	private Timestamp loadDate;

	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private Timestamp startTime;

	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private Timestamp endTime;

	private Integer type;

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
	public Timestamp getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Timestamp executeTime) {
		this.executeTime = executeTime;
	}

	@ApiModelProperty("状态")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@ApiModelProperty("实例类型")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public static void main(String[] args) {
		System.out.println(TimeZone.getDefault().toString());
	}
}
