package com.iwellmass.idc.app.vo;

import java.util.List;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.model.Task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleTaskVO {
	@ApiModelProperty("任务id")
	private String taskId;
	@ApiModelProperty("任务域")
	private String taskGroup;
	@ApiModelProperty("任务名称")
	private String taskName;
	@ApiModelProperty("任务参数")
	private List<ExecParam> parameter;

	public SimpleTaskVO(Task task) {
		taskId = task.getTaskId();
		taskGroup = task.getTaskGroup();
		taskName = task.getTaskName();
		parameter = task.getParameter();
	}
}
