package com.iwellmass.idc.scheduler.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "idc_workflow_edge")
public class WorkflowEdge {

	/**
	 * 自增ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * 工作流ID
	 */
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