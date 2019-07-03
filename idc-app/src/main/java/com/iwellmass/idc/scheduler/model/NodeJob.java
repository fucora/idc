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

	@Column(name = "task_id")
	private String taskId;
	
	@Column(name = "node_id")
	private String nodeId;
	
	@Column(name = "container")
	private String container;
	
	@Column(name = "main_id")
	private String mainId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "task_id", referencedColumnName = "pid", insertable = false, updatable = false),
		@JoinColumn(name = "node_id", referencedColumnName = "id", insertable = false, updatable = false)
	})
	private NodeTask nodeTask;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Job mainJob;

	public NodeJob() {
	}

	public NodeJob(String container, NodeTask nodeTask) {
		super(id(container, nodeTask.getId()), nodeTask);
		// 设置 ID
		this.container = container;
		this.nodeId = nodeTask.getId();
	}

	@Override
	public void start0() {
	}

	@Override
	public void renew() {
	}

	@Override
	public void finish() {
	}

	@Override
	public void fail() {

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
