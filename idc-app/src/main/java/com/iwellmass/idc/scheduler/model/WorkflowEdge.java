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
@Table(name = "idc_workflow_edge")
public class WorkflowEdge {

	
	/**
	 * 工作流ID
	 */
	@Id
	@Column
	private String pid;
	
	
	/**
	 * 边 ID
	 */
	@Id
	private String id;

	/**
	 * Source Node
	 */
	@Column
	private String source;

	/**
	 * Target Node
	 */
	@Column
	private String target;
}