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

@Entity
@Table(name = "t_idc_workflow_edge")
@Setter
@Getter
public class WorkflowEdge implements Serializable, Comparable<WorkflowEdge>{
	
	private static final long serialVersionUID = 866853625098155270L;
	
	public static final TaskKey START = new TaskKey("start", "idc");
	
	public static final TaskKey END = new TaskKey("end", "idc");

	public static final String CTRL_JOIN_GROUP = "idc-ctrl";

	public static final TaskKey CTRL_JOIN = new TaskKey("join", "idc");
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "src_task_id")
	private String srcTaskId;
	
	@Column(name = "src_task_group")
	private String srcTaskGroup;
	
	@Column(name = "task_id")
	private String taskId;
	
	@Column(name = "task_group")
	private String taskGroup;

	@Column(name = "parent_task_id")
	private String parentTaskId;

	@Column(name = "parent_task_group")
	private String parentTaskGroup;

	public void setSrcTaskKey(TaskKey tk) {
		this.srcTaskId = tk.getTaskId();
		this.srcTaskGroup = tk.getTaskGroup();
	}
	
	public void setTaskKey(TaskKey tk) {
		this.taskId = tk.getTaskId();
		this.taskGroup = tk.getTaskGroup();
	}

	public void setParentTaskKey(TaskKey tk) {
        this.parentTaskId = tk.getTaskId();
        this.parentTaskGroup = tk.getTaskGroup();
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

    @Transient
    @JsonIgnore
    public TaskKey getParentTaskKey() {
        return new TaskKey(parentTaskId, parentTaskGroup);
    }

	@Override
	public int compareTo(WorkflowEdge o) {
		int i = srcTaskGroup.compareTo(o.srcTaskId);
		if (i == 0) {
			i = srcTaskId.compareTo(o.srcTaskId);
			if (i == 0) {
				i = taskGroup.compareTo(o.taskGroup);
				if (i == 0) {
					i = taskId.compareTo(o.taskId);
				}
			}
		}
		return i;
	}

	
}
