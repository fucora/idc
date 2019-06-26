package com.iwellmass.idc.scheduler.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class AbstractTask {

	/**
	 * 业务ID
	 */
	@Column(name = "task_id")
	String taskId;

	/**
	 * 业务域
	 */
	@Column(name = "domain")
	String domain;
	
	/**
	 * 任务类型
	 */
	@Column(name = "task_type")
	@Enumerated(EnumType.STRING)
	private TaskType taskType;
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id", insertable = false, updatable = false)
	private Workflow workflow;
	
	/**
	 * 调度方式
	 */
	@Column(name = "schedule_type")
	@Enumerated(EnumType.STRING)
	private ScheduleType scheduleType;
}
