package com.iwellmass.idc.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "t_idc_workflow_edge")
public class WorkflowEdge implements Serializable{
	
	private static final long serialVersionUID = 866853625098155270L;

	public WorkflowEdge(String workflowId, TaskDependency taskDependency) {
		this.workflowId = workflowId;
		this.srcTaskId = taskDependency.getSrcTaskId();
		this.srcTaskGroup = taskDependency.getSrcTaskGroup();
		this.taskId = taskDependency.getTaskId();
		this.taskGroup = taskDependency.getTaskGroup();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private String workflowId;
	
	private String srcTaskId;
	
	private String srcTaskGroup;
	
	private String taskId;
	
	private String taskGroup;
	
}
