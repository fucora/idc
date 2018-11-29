package com.iwellmass.idc.app.vo;

import java.util.List;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.ScheduleStatus;
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
	
	@ApiModelProperty("调度信息")
	private ScheduleStatus status;
	
	@ApiModelProperty("依赖信息")
	private List<JobBarrierVO> barriers;
}
