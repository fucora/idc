package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

public enum TaskType {

	@ApiModelProperty("节点任务")
	NODE_TASK,
	
	@ApiModelProperty("工作流任务")
	WORKFLOW,
	
	@ApiModelProperty("工作流子任务")
	SUB_TASK;
}