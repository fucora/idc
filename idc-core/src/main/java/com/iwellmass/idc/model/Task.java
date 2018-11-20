package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Task {

	@ApiModelProperty("业务ID")
	private String taskId;

	@ApiModelProperty("业务域")
	private String taskGroup;

	@ApiModelProperty("任务名称")
	private String taskName;

	@ApiModelProperty("任务描述")
	private String description;

	@ApiModelProperty("任务类型，工作流任务，节点任务")
	private TaskType taskType;

	@ApiModelProperty("业务类型，业务方自定义")
	private String contentType;
	
	@ApiModelProperty("执行方式")
	private DispatchType dispatchType;
	
	@ApiModelProperty("工作流ID")
	private Integer workflowId;

	public TaskKey getTaskKey() {
		return new TaskKey(getTaskId(), getTaskGroup());
	};
	
	public void setTaskKey(TaskKey taskKey) {
		setTaskId(taskKey.getTaskId());
		setTaskGroup(taskKey.getGroupId());
	};
}
