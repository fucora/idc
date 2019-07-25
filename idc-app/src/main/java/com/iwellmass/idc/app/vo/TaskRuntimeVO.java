package com.iwellmass.idc.app.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.scheduler.model.TaskState;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("调度计划运行时信息")
public class TaskRuntimeVO  {

	@ApiModelProperty("计划")
	private String taskName;
	
	@ApiModelProperty("工作流ID")
	private String taskId;

	@ApiModelProperty("业务域")
	private String taskGroup;
	
	@ApiModelProperty("责任人")
	private String assignee;
	
	@ApiModelProperty("上一批次")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private LocalDateTime prevFireTime;
	
	@ApiModelProperty("下一批次")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private LocalDateTime nextFireTime;
	
	@ApiModelProperty("调度状态")
	private TaskState state;
	
}
