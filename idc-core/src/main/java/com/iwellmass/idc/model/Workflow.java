package com.iwellmass.idc.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "t_idc_workflow")
public class Workflow {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer workflowId;
	
	private String taskId;
	
	private String taskGroup;
	
	// 前端用，流程都画图
	private String graph;
}