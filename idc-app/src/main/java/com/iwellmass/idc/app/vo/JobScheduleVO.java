package com.iwellmass.idc.app.vo;

import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.TaskType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobScheduleVO {

	@ApiModelProperty("业务ID")
	private String taskId;

	@ApiModelProperty("业务域")
	private String taskGroup;
	
	@ApiModelProperty("任务类型")
	private TaskType taskType;

	@ApiModelProperty("业务类型")
	private String contentType;
	
	@ApiModelProperty("执行方式")
	private DispatchType dispatchType;
	
	@ApiModelProperty("workflow_id")
	private String workflowId;
	
	private ScheduleProperties scheduleConfig;
	
}
