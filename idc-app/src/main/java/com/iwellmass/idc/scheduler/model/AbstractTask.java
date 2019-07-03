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
public abstract class AbstractTask {

	/**
	 * 业务ID
	 */
	@Column(name = "task_id")
	String taskId;
	/**
	 * 任务类型
	 */
	@Column(name = "task_type")
	@Enumerated(EnumType.STRING)
	private TaskType taskType;
	
	/**
	 * 业务域
	 */
	@Column(name = "domain")
	String domain;
	
	@Column(name = "description")
	String description;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id", insertable = false, updatable = false)
	private Workflow workflow;
}
