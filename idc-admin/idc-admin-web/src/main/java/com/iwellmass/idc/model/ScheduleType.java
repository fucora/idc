package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * 调度类型
 */
public enum ScheduleType {

	@ApiModelProperty("月调度")
	MONTHLY,

	@ApiModelProperty("周调度")
	WEEKLY,

	@ApiModelProperty("日调度")
	DAILY,

	@ApiModelProperty("小时调度")
	HOURLY,

	@ApiModelProperty("自定义 CRON 表达式")
	CRON;
	
}
