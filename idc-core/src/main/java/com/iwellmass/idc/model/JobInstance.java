package com.iwellmass.idc.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.JSON;
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
	
	// ~~ 调度相关（主） ~~
	@ApiModelProperty("计划ID")
	@Column(name = "job_id")
	private String jobId;

	@ApiModelProperty("计划Group")
	@Column(name = "job_group")
	private String jobGroup;
	
	@ApiModelProperty("计划名称")
	@Column(name = "job_name")
	private String jobName;

	@ApiModelProperty("业务日期")
	@Column(name = "load_date")
	private String loadDate;
	
	@ApiModelProperty("周期类型")
	@Column(name = "schedule_type")
	@Enumerated(EnumType.STRING)
	private ScheduleType scheduleType;

	@ApiModelProperty("参数设置")
	@Column(name = "parameter")
	private String parameter;
	
	@ApiModelProperty("责任人")
	@Column(name = "assignee")
	private String assignee;
	
	// ~~ 运行相关 ~~
	@ApiModelProperty("本次调度日期")
	@Column(name = "should_fire_time")
	private Long shouldFireTime;
	
	@ApiModelProperty("上次调度日期")
	@Column(name = "prev_fire_time")
	private Long prevFireTime;
	
	@ApiModelProperty("开始时间")
	@Column(name = "start_time")
	private LocalDateTime startTime;

	@ApiModelProperty("结束时间")
	@Column(name = "end_time")
	private LocalDateTime endTime;

	@ApiModelProperty("任务实例状态")
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private JobInstanceStatus status;
	
	@ApiModelProperty("父实例ID")
	@Column(name = "main_instance_id")
	private Integer mainInstanceId;
	
	// ~~ 任务相关 ~~
	@ApiModelProperty("任务ID")
	@Column(name = "task_id")
	private String taskId;

	@ApiModelProperty("任务域")
	@Column(name = "task_group", length = 50)
	private String taskGroup;

	@ApiModelProperty("任务类型")
	@Column(name = "task_type")
	@Enumerated(EnumType.STRING)
	private TaskType taskType;

	@ApiModelProperty("ContentType")
	@Column(name = "content_type")
	private String contentType;
	
	@ApiModelProperty("执行方式")
	@Column(name = "dispatch_type")
	@Enumerated(EnumType.STRING)
	private DispatchType dispatchType;
	
	@Transient
	public <T> T getParameterObject(Class<T> type) {
		if (this.parameter == null || this.parameter.isEmpty()) {
			return null;
		}
		return JSON.parseObject(this.getParameter(), type);
	}

	@Transient
	public DispatchType getDispatchType() {
		return dispatchType;
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
	@JsonIgnore
	public TaskKey getTaskKey() {
		return new TaskKey(taskId, taskGroup);
	}
	
	public void setTaskKey(TaskKey taskKey) {
		this.taskId = taskKey.getTaskId();
		this.taskGroup = taskKey.getTaskGroup();
	}
	
	@Override
	public String toString() {
		return "JobInstance [instanceId=" + instanceId + ", jobId=" + jobId + ", jobGroup=" + jobGroup + ", loadDate="
				+ loadDate + "]";
	}
}
