package com.iwellmass.idc.app.vo;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.Task;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRuntimeVO {

	@ApiModelProperty("任务信息")
	private Task task;
	
	@ApiModelProperty("调度信息")
	private Job job;
	
	@ApiModelProperty("运行时信息")
	private JobRuntime jobRuntime;
	
}
