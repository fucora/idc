package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(TaskKey.class)
@Table(name = "t_idc_task")
public class Task {

	@Id
	@ApiModelProperty("业务ID")
	@Column(name = "task_id")
	private String taskId;

	@Id
	@ApiModelProperty("业务域")
	@Column(name = "task_group")
	private String taskGroup;

	@ApiModelProperty("任务名称")
	@Column(name = "task_name")
	private String taskName;

	@ApiModelProperty("任务描述")
	@Column(name = "description")
	private String description;

	@ApiModelProperty("任务类型，工作流任务，节点任务")
	@Column(name = "task_type")
	@Enumerated(EnumType.STRING)
	private TaskType taskType;

	@ApiModelProperty("业务类型，业务方自定义")
	@Column(name = "content_type")
	private String contentType;
	
	@ApiModelProperty("执行方式")
	@Column(name = "dispatch_type")
	@Enumerated(EnumType.STRING)
	private DispatchType dispatchType;
	
	@ApiModelProperty("工作流ID")
	@Column(name = "workflow_id")
	private String workflowId;
	
	@Transient
	@ApiModelProperty("编辑ID")
	private String graphId;
	
	@Transient
	@ApiModelProperty("编辑图")
	private String graph;

	@Transient
	@JsonIgnore
	public TaskKey getTaskKey() {
		return new TaskKey(getTaskId(), getTaskGroup());
	}
	
	public void setTaskKey(TaskKey taskKey) {
		setTaskId(taskKey.getTaskId());
		setTaskGroup(taskKey.getTaskGroup());
	}
}