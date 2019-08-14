package com.iwellmass.idc.scheduler.model;

import javax.persistence.*;

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
	 * 调度名称，主键
	 */
	@Id
	@Column(name = "task_name")
	protected String taskName;

	/**
	 * 任务ID
	 */
	@Id
	@Column(name = "workflow_id")
	String workflowId;

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
	@JoinColumn(name = "workflow_id", insertable = false, updatable = false)
	private Workflow workflow;
}
