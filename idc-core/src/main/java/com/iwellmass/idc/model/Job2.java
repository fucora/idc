package com.iwellmass.idc.model;

public interface Job2 {


	public String getJobId();
	
	public String getJobGroup();
	
	
/*	// ~~ 任务属性 ~~
	@Id
	@Column(name = "job_id", length = 50)
	private String jobId;

	@Id
	@Column(name = "job_group", length = 50)
	private String jobGroup;

	@ApiModelProperty("依赖")
	@Transient
	private List<JobDependency> dependencies;

	@ApiModelProperty("创建日期")
	@Column(name = "createtime")
	private LocalDateTime createTime;

	@ApiModelProperty("更新时间")
	@Column(name = "update_time")
	private LocalDateTime updateTime;

	@ApiModelProperty("参数")
	@Column(name = "parameter", length = 4000)
	private String parameter;

	@ApiModelProperty("所属工作流 ID")
	@Column(name = "workflow_id")
	private Integer workflowId;
	
	@ApiModelProperty("责任人")
	@Column(name = "assignee")
	private String assignee;

	// ~~ 节点任务，流程主任务 ~~
	@ApiModelProperty("调度配置")
	@Column(name = "schedule_config")
	@Convert(converter = SchedulePropertiesConverter.class)
	private ScheduleProperties scheduleProperties;
	
	@ApiModelProperty("执行方式")
	@Column(name = "dispatch_type")
	private DispatchType dispatchType;

	@ApiModelProperty("调度类型")
	@Column(name = "schedule_type")
	private ScheduleType scheduleType;
	
	// ~~ JobDetails 属性 ~~
	@ApiModelProperty("业务ID")
	@Column(name = "task_id")
	private String taskId;

	@ApiModelProperty("业务域")
	@Column(name = "group_id")
	private String groupId;

	@ApiModelProperty("任务名称")
	@Column(name = "task_name", length = 100)
	private String taskName;

	@ApiModelProperty("任务描述")
	@Column(name = "description", length = 300)
	private String description;

	@ApiModelProperty("任务类型，工作流任务，节点任务")
	@Column(name = "task_type")
	private TaskType taskType;

	@ApiModelProperty("业务类型，业务方自定义")
	@Column(name = "content_type")
	private String contentType;

	// ~~ SecondaryTable 运行时信息 ~~
	@ApiModelProperty("生效日期始 yyyy-MM-dd HH:mm:ss")
	@Column(table = TABLE_TRIGGERS, name = "START_TIME")
	@Convert(converter = LocalDateTimeMillsConverter.class)
	private LocalDateTime startTime;

	@ApiModelProperty("生效日期止, yyyy-MM-dd HH:mm:ss")
	@Column(table = TABLE_TRIGGERS, name = "END_TIME")
	@Convert(converter = LocalDateTimeMillsConverter.class)
	private LocalDateTime endTime;

	@ApiModelProperty("最近一次业务日期")
	@Column(table = TABLE_TRIGGERS, name = "PREV_FIRE_TIME")
	@Convert(converter = LocalDateTimeMillsConverter.class)
	private LocalDateTime prevLoadDate;

	@ApiModelProperty("下一次业务日期")
	@Column(table = TABLE_TRIGGERS, name = "NEXT_FIRE_TIME")
	@Convert(converter = LocalDateTimeMillsConverter.class)
	private LocalDateTime nextLoadDate;

	@ApiModelProperty("任务状态")
	@Column(table = TABLE_TRIGGERS, name = "TRIGGER_STATE")
	@Convert(converter = ScheduleStatusConverter.class)
	private ScheduleStatus status = ScheduleStatus.NONE;

	@Transient
	public JobKey getJobKey() {
		return new JobKey(jobId, jobGroup);
	}
	
	public void setJobKey(JobKey pk) {
		this.jobId = pk.getJobId();
		this.jobGroup = pk.getJobGroup();
	}
	
	@Transient
	public TaskKey getTaskKey() {
		return new TaskKey(taskId, groupId);
	}
	
	public void setTaskKey(TaskKey taskKey) {
		this.taskId = taskKey.getTaskId();
		this.groupId = taskKey.getGroupId();
	}

	@Override
	public String toString() {
		return "Job [jobId=" + jobId + ", jobGroup=" + jobGroup + ", taskName=" + taskName + "]";
	}*/
}
