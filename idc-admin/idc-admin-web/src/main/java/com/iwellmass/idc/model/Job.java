package com.iwellmass.idc.model;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "t_idc_job") // DDC_TASK
public class Job {

	private Integer id;

	private String taskName;

	private String description;

	private String taskId;

	private TaskType taskType;

	private String contentType;

	private ScheduleType scheduleType;

	private ScheduleProperties scheduleProperties;

	private Timestamp startTime;

	private Timestamp endTime;

	private Timestamp createTime;

	private Timestamp updateTime;

	private Integer groupId;

	private Integer workflowId;

	private String assignee;

	private Integer status;

	private Set<JobDependency> dependencies;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty("任务ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ApiModelProperty("任务名称")
	@Column(name = "name")
	public String getJobName() {
		return taskName;
	}

	public void setJobName(String taskName) {
		this.taskName = taskName;
	}

	@ApiModelProperty("任务描述")
	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ApiModelProperty("业务标识")
	@Column(name = "task_id")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@ApiModelProperty("任务类型，工作流任务，节点任务")
	@Column(name = "task_type")
	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	@ApiModelProperty("业务类型，DATA_SYNC、SPARK_SQL")
	@Column(name = "content_type")
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@ApiModelProperty("调度类型")
	@Column(name = "schedule_type")
	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}

	@ApiModelProperty("调度配置")
	@Column(name = "schedule_config")
	@Convert(converter = SchedulePropertiesConverter.class)
	public ScheduleProperties getScheduleProperties() {
		return scheduleProperties;
	}

	public void setScheduleProperties(ScheduleProperties scheduleProperties) {
		this.scheduleProperties = scheduleProperties;
	}

	@ApiModelProperty("生效日期始 yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	@Column(name = "start_time")
	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	@ApiModelProperty("生效日期止, yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	@Column(name = "end_time")
	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	@ApiModelProperty("创建日期")
	@Column(name = "createtime")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	@ApiModelProperty("资源组")
	@Column(name = "group_id")
	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	@Column(name = "workflow_id")
	@ApiModelProperty("所属工作流 ID")
	public Integer getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Integer workflowId) {
		this.workflowId = workflowId;
	}

	@ApiModelProperty("依赖")
	@Transient
	public Set<JobDependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(Set<JobDependency> dependencies) {
		this.dependencies = dependencies;
	}

	public boolean hasDependencies() {
		return !(dependencies == null || dependencies.isEmpty());
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	@ApiModelProperty("更新时间")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}


	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@ApiModelProperty("任务状态")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
