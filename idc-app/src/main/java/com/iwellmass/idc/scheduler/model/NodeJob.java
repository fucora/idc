package com.iwellmass.idc.scheduler.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "idc_node_job")
public class NodeJob extends AbstractJob {

	@Column(name = "container")
	private String container;

	@Column(name = "node_id")
	private String nodeId;

	public NodeJob() {
	}

	public NodeJob(String container, NodeTask nodeTask) {
		super(id(container, nodeTask.getId()), nodeTask);
		// 设置 ID
		this.container = container;
		this.nodeId = nodeTask.getId();
	}

	@Override
	public void start() {
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
}
