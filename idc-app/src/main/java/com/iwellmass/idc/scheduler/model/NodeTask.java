package com.iwellmass.idc.scheduler.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(NodeTaskID.class)
@Table(name = "idc_node_task")
public class NodeTask extends AbstractTask {

	public static final NodeTaskID START = new NodeTaskID();
	public static final NodeTaskID END = new NodeTaskID();

	/**
	 * 工作流
	 */
	@Id
	@Column(name = "pid")
	private String pid;
	
	/**
	 * node id
	 */
	@Id
	@Column(name = "id")
	private String id;
	
}
