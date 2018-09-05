package com.iwellmass.idc.model;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "t_idc_job")
@IdClass(JobPK.class)
public class Job {

	public static final String DEFAULT_GROUP = "default";

	// ~~ 任务属性 ~~
	private String taskId;

	private String groupId = DEFAULT_GROUP;
	
	private String domain;
	
	private String taskName;

	private String description;

	private TaskType taskType;

	private ContentType contentType;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

	private Integer workflowId;

	private String assignee;

	private Set<JobDependency> dependencies;

	// ~~ 调度属性 ~~
	private ScheduleType scheduleType;

	private ScheduleProperties scheduleProperties;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private ScheduleStatus status;

	private LocalDateTime prevLoadDate;

	private LocalDateTime nextLoadDate;

	@Id
	@ApiModelProperty("业务ID")
	@Column(name = "task_id", length = 50)
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@ApiModelProperty("业务组")
	@Id
	@Column(name = "group_id", length = 50)
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	

	@ApiModelProperty("所属域")
	@Column(name = "domain", length = 50)
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@ApiModelProperty("任务名称")
	@Column(name = "name", length = 100)
	public String getJobName() {
		return taskName;
	}

	public void setJobName(String taskName) {
		this.taskName = taskName;
	}

	@ApiModelProperty("任务描述")
	@Column(name = "description", length = 300)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "start_time")
	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	@ApiModelProperty("生效日期止, yyyyMMdd")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "end_time")
	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	@ApiModelProperty("创建日期")
	@Column(name = "createtime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
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
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	@ApiModelProperty("任务状态")
	@Column(name = "status")
	public ScheduleStatus getStatus() {
		return status;
	}

	public void setStatus(ScheduleStatus status) {
		this.status = status;
	}

	@ApiModelProperty("最近一次业务日期")
	@Column(name = "prev_load_date")
	public LocalDateTime getPrevLoadDate() {
		return prevLoadDate;
	}

	public void setPrevLoadDate(LocalDateTime prevLoadDate) {
		this.prevLoadDate = prevLoadDate;
	}

	@ApiModelProperty("下一次业务日期")
	@Column(name = "next_load_date")
	public LocalDateTime getNextLoadDate() {
		return nextLoadDate;
	}

	public void setNextLoadDate(LocalDateTime nextLoadDate) {
		this.nextLoadDate = nextLoadDate;
	}

}