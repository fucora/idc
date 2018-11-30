package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Entity
@IdClass(TaskKey.class)
@Table(name = "t_idc_task")
public class Task {

    public Task() {
    }

    public Task(TaskCreateVO taskCreateVO){
		this.taskId = String.valueOf(System.currentTimeMillis());
		this.taskGroup = taskCreateVO.getTaskGroup();
		this.taskName = taskCreateVO.getTaskName();
		this.description = taskCreateVO.getDescription();
		this.taskType = taskCreateVO.getTaskType();
		this.contentType = taskCreateVO.getContentType();
		this.dispatchType = taskCreateVO.getDispatchType();
		this.workflowId = taskCreateVO.getWorkflowId();
	}

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
	private TaskType taskType;

	@ApiModelProperty("业务类型，业务方自定义")
	@Column(name = "content_type")
	private String contentType;
	
	@ApiModelProperty("执行方式")
	@Column(name = "dispatch_type")
	private DispatchType dispatchType;
	
	@ApiModelProperty("工作流ID")
	@Column(name = "workflow_id")
	private Integer workflowId;

	@Transient
	public TaskKey getTaskKey() {
		return new TaskKey(getTaskId(), getTaskGroup());
	}
	
	public void setTaskKey(TaskKey taskKey) {
		setTaskId(taskKey.getTaskId());
		setTaskGroup(taskKey.getTaskGroup());
	};
}