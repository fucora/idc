package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

public enum ScheduleStatus {

	@ApiModelProperty("正常")
	NORMAL,
	
	@ApiModelProperty("冻结")
	PAUSED,
	
	@ApiModelProperty("完结")
	COMPLETE,
	
	@ApiModelProperty("调度异常")
	ERROR,
	
	@ApiModelProperty("调度阻塞")
	BLOCKED
	
	
	
	// NONE, NORMAL, PAUSED, COMPLETE, ERROR, BLOCKED 
}
