package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * 任务类型
 */
public enum JobType {

	@ApiModelProperty("周期任务")
	CRON,
	
	@ApiModelProperty("手动任务")
	MANUAL;
}
