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

/**
 *抽象任务，描述一个可被执行的任务所需要的最基本信息
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractTask {

	/**
	 * 任务ID
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
	 * 任务域
	 */
	@Column(name = "domain")
	String domain;
	
	/**
	 * 任务描述
	 */
	@Column(name = "description")
	String description;
	
	/**
	 * 所指向的工作流（只有在 taskType == WORKFLOW 时才会有值）
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id", insertable = false, updatable = false)
	private Workflow workflow;
}
