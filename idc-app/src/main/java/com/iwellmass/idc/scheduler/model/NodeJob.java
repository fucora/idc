package com.iwellmass.idc.scheduler.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 子任务（可由 Job 触发，也可由 NodeJob 触发），需要将 {@link NodeJob#getId()} 设置到子任务的 {@link NodeJob#container} 字段
 */
@Getter
@Setter
@Entity
@Table(name = "idc_plan_instance_node")
public class NodeJob extends AbstractJob {

	/**
	 *  任务ID（Task.taskId）
	 */
	@Column(name = "task_id")
	private String taskId;

	/**
	 * 工作流结点ID（NodeTask.id）
	 */
	@Column(name = "node_id")
	private String nodeId;

	/**
	 * 主 Job ID（Job.id）
	 */
	@Column(name = "main_id")
	private String mainId;

	/**
	 * 父 Job ID（Job.id OR NodeJob.id）
	 */
	@Column(name = "container")
	private String container;

	/**
	 * 关联的子任务
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "task_id", referencedColumnName = "pid", insertable = false, updatable = false),
			@JoinColumn(name = "node_id", referencedColumnName = "id", insertable = false, updatable = false) })
	private NodeTask nodeTask;

	public NodeJob() {
	}

	public NodeJob(String container, NodeTask nodeTask) {
		super(id(container, nodeTask.getId()), nodeTask);
		// 设置 ID
		this.container = container;
		this.nodeId = nodeTask.getId();
	}

	private static final String id(String container, String nodeId) {
		// TODO hash 对齐
		return container + "_" + nodeId;
	}

	@Override
	public AbstractTask getTask() {
		return getNodeTask();
	}
}
