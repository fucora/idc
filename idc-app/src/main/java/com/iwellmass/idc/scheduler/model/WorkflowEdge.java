package com.iwellmass.idc.scheduler.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	 * 自增ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	/**
	 * 工作流ID
	 */
	@Id
	@Column
	private String pid;

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