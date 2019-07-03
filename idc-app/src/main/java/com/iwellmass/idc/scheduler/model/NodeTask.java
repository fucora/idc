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
@IdClass(WfID.class)
@Table(name = "idc_node_task")
public class NodeTask extends AbstractTask {
	
	public static final String START = "START";
	public static final String END = "END";

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
