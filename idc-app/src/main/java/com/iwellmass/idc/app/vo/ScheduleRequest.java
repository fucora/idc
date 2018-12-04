package com.iwellmass.idc.app.vo;

import javax.validation.constraints.NotNull;

import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.Task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleRequest {
	
	@NotNull(message = "任务信息不能为空")
	private Task task;
	
	@NotNull(message = "调度信息不能为空")
	private ScheduleProperties scheduleConfig;
	
}
