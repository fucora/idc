package com.iwellmass.idc.scheduler.model;

import javax.persistence.*;

import com.iwellmass.common.exception.AppException;
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

	public Task asTask() {
		if (this instanceof Task) {
			return (Task)this;
		} else {
			throw new AppException("该task的类型错误"  + this.getClass());
		}
	}

	public NodeTask asNodeTask() {
		if (this instanceof NodeTask) {
			return (NodeTask)this;
		} else {
			throw new AppException("该task的类型错误"  + this.getClass());
		}
	}
}
