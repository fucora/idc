package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

// copy-of-YARN :) NEW, NEW_SAVING, SUBMITTED, ACCEPTED, RUNNING, FINISHED, FAILED, KILLED
public enum JobInstanceStatus {
	
	
	@ApiModelProperty("等待派发")
	NEW,
	
	@ApiModelProperty("已派发")
	ACCEPTED,
	
	@ApiModelProperty("运行中")
	RUNNING,
	
	@ApiModelProperty("成功")
	FINISHED,
	
	@ApiModelProperty("失败")
	FAILED
}
