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

@Getter
@Setter
@Entity
@Table(name = "idc_node_job")
public class NodeJob extends AbstractJob {

	/**
	 * Task ID
	 */
	@Column(name = "task_id")
	private String taskId;

	/**
	 * 工作流图结点ID
	 */
	@Column(name = "node_id")
	private String nodeId;

	/**
	 * 主 Job ID
	 */
	@Column(name = "main_id")
	private String mainId;

	/**
	 * 运行时父 Job ID
	 */
	@Column(name = "container")
	private String container;

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
