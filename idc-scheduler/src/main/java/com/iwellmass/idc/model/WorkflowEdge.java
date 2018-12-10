package com.iwellmass.idc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "t_idc_workflow_edge")
public class WorkflowEdge implements Serializable{
	
	private static final long serialVersionUID = 866853625098155270L;
	
	public static final TaskKey START = new TaskKey("start", "idc");
	
	public static final TaskKey END = new TaskKey("end", "idc");
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "workflow_id")
	private String workflowId;
	
	@Column(name = "src_task_id")
	private String srcTaskId;
	
	@Column(name = "src_task_group")
	private String srcTaskGroup;
	
	@Column(name = "task_id")
	private String taskId;
	
	@Column(name = "task_group")
	private String taskGroup;

	
	public void setSrcTaskKey(TaskKey tk) {
		this.srcTaskId = tk.getTaskId();
		this.srcTaskGroup = tk.getTaskGroup();
	}
	
	public void setTaskKey(TaskKey tk) {
		this.taskId = tk.getTaskId();
		this.taskGroup = tk.getTaskGroup();
	}
	
	@Transient
	@JsonIgnore
	public TaskKey getSrcTaskKey() {
		return new TaskKey(srcTaskId, srcTaskGroup);
	}
	
	@Transient
	@JsonIgnore
	public TaskKey getTaskKey() {
		return new TaskKey(taskId, taskGroup);
	}
	
	
}
