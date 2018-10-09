package com.iwellmass.idc.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

/**
 * 任务实例
 */
@Entity
@Table(name = "t_idc_job_instance")
public class JobInstance {

	private Integer instanceId;

	// ~~ 调度相关 ~~
	private String jobId;
	
	private String jobGroup;
	
	private Long shouldFireTime;
	
	private LocalDateTime loadDate;
	
	
	// ~~ 任务相关 ~~
	private String taskId;

	private String groupId;

	private String taskName;

	private String description;

	private TaskType taskType;

	private String contentType;

	private Integer workflowId;

	private String assignee;

	private ScheduleType scheduleType;

	
	// ~~ 运行相关  ~~
	
	private String parameter;
	
	private LocalDateTime startTime;
	
	private LocalDateTime endTime;
	
	private JobInstanceStatus status;
	
	private LocalDateTime nextLoadDate;
	
	private JobInstanceType instanceType;
	
	@ApiModelProperty("执行ID")
	@Id
	@Column(name = "instance_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getInstanceId() {
		return instanceId;
	}
	
	public void setInstanceId(Integer instanceId) {
		this.instanceId = instanceId;
	}
	
	@Column(name = "job_id")
	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobName) {
		this.jobId = jobName;
	}
	
	@Column(name = "job_group")
	public String getJobGroup() {
		return this.jobGroup;
	}
	
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	
	@ApiModelProperty("任务 ID")
	@Column(name = "task_id")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Column(name = "group_id", length = 50)
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	@Column(name = "task_name", length = 200)
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@Column(name = "task_type")
	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	@ApiModelProperty("描述")
	@Column(name = "description", length = 500)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ApiModelProperty("ContentType")
	@Column(name = "content_type")
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@ApiModelProperty("工作流ID")
	@Column(name = "workflow_id")
	public Integer getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Integer workflowId) {
		this.workflowId = workflowId;
	}

	@ApiModelProperty("业务日期")
	@Column(name = "load_date")
	@JsonFormat(pattern = "yyyyMMddHHmmss", timezone = "GMT+8")
	public LocalDateTime getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(LocalDateTime loadDate) {
		this.loadDate = loadDate;
	}

	@ApiModelProperty("schedule_type")
	@Column(name = "schedule_type")
	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}

	@ApiModelProperty("上次触发时间")
	@Column(name = "next_load_date")
	public LocalDateTime getNextLoadDate() {
		return nextLoadDate;
	}

	public void setNextLoadDate(LocalDateTime nextLoadDate) {
		this.nextLoadDate = nextLoadDate;
	}

	@ApiModelProperty("参数设置")
	@Column(name = "parameter", length = 4000)
	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	@Transient
	public <T> T getParameterObject(Class<T> type) {
		if (this.parameter == null || this.parameter.isEmpty()) {
			return null;
		}
		return JSON.parseObject(this.getParameter(), type);
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
	@Column(name = "instance_type")
	public JobInstanceType getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(JobInstanceType instanceType) {
		this.instanceType = instanceType;
	}

	@Column(name = "should_fire_time")
	public Long getShouldFireTime() {
		return this.shouldFireTime;
	}

	public void setShouldFireTime(Long shouldFireTime) {
		this.shouldFireTime = shouldFireTime;
	}
	
	@Transient
	public DispatchType getDispatchType() {
		return DispatchType.valueOf(instanceType.toString());
	}

	
	public void setJobPK(JobPK jobKey) {
		this.jobId = jobKey.getJobId();
		this.jobGroup = jobKey.getJobGroup();
	}
	
	@Transient
	@JsonIgnore
	public JobPK getJobPK() {
		return new JobPK(jobId, jobGroup);
	}
	
	@Override
	public String toString() {
		return "JobInstance [instanceId=" + instanceId + ", taskId=" + taskId + ", groupId=" + groupId + ", loadDate="
				+ loadDate + "]";
	}

}
