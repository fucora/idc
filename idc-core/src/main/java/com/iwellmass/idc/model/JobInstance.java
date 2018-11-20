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
import lombok.Data;

/**
 * 任务实例
 */
@Data
@Entity
@Table(name = "t_idc_job_instance")
public class JobInstance {

	@ApiModelProperty("执行ID")
	@Id
	@Column(name = "instance_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer instanceId;
	
	// ~~ 调度相关 ~~
	@Column(name = "job_id")
	private String jobId;

	@Column(name = "job_group")
	private String jobGroup;

	@Column(name = "should_fire_time")
	private Long shouldFireTime;

	@ApiModelProperty("业务日期")
	@Column(name = "load_date")
	@JsonFormat(pattern = "yyyyMMddHHmmss", timezone = "GMT+8")
	private LocalDateTime loadDate;

	@ApiModelProperty("参数设置")
	@Column(name = "parameter", length = 4000)
	private String parameter;
	
	// ~~ 任务相关 ~~
	@ApiModelProperty("任务 ID")
	@Column(name = "task_id")
	private String taskId;

	@Column(name = "group_id", length = 50)
	private String groupId;

	@Column(name = "task_name", length = 200)
	private String taskName;

	@ApiModelProperty("描述")
	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "task_type")
	private TaskType taskType;

	@ApiModelProperty("ContentType")
	@Column(name = "content_type")
	private String contentType;

	@ApiModelProperty("工作流ID")
	@Column(name = "workflow_id")
	private Integer workflowId;

	@ApiModelProperty("责任人")
	@Column(name = "assignee")
	private String assignee;

	@ApiModelProperty("schedule_type")
	@Column(name = "schedule_type")
	private ScheduleType scheduleType;

	
	// ~~ 运行相关 ~~
	@ApiModelProperty("开始时间")
	@Column(name = "start_time")
	private LocalDateTime startTime;

	@ApiModelProperty("结束时间")
	@Column(name = "end_time")
	private LocalDateTime endTime;

	@ApiModelProperty("任务实例状态")
	@Column(name = "status")
	private JobInstanceStatus status;

	@ApiModelProperty("实例类型")
	@Column(name = "instance_type")
	private DispatchType instanceType;
	
	
	@Transient
	public <T> T getParameterObject(Class<T> type) {
		if (this.parameter == null || this.parameter.isEmpty()) {
			return null;
		}
		return JSON.parseObject(this.getParameter(), type);
	}

	@Transient
	public DispatchType getDispatchType() {
		return instanceType;
	}

	@Transient
	@JsonIgnore
	public JobKey getJobKey() {
		return new JobKey(jobId, jobGroup);
	}

	public void setJobKey(JobKey jobKey) {
		this.jobId = jobKey.getJobId();
		this.jobGroup = jobKey.getJobGroup();
	}
	
	@Transient
	public TaskKey getTaskKey() {
		return new TaskKey(taskId, groupId);
	}
	

	@Override
	public String toString() {
		return "JobInstance [instanceId=" + instanceId + ", jobId=" + jobId + ", jobGroup=" + jobGroup + ", loadDate="
				+ loadDate + "]";
	}
}
