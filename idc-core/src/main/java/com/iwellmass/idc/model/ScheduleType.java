package com.iwellmass.idc.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.swagger.annotations.ApiModelProperty;

/**
 * 周期类型
 */
public enum ScheduleType {

	@ApiModelProperty("月调度")
	MONTHLY("yyyyMM"),

	@ApiModelProperty("周调度")
	WEEKLY("yyyyMMddHHmmss"),

	@ApiModelProperty("日调度")
	DAILY("yyyyMMdd"),

	@ApiModelProperty("小时调度")
	HOURLY("yyyyMMddHHmmss"),

	@ApiModelProperty("自定义")
	CUSTOMER("yyyyMMddHHmmss");
	
	private final DateTimeFormatter fmt;
	
	private ScheduleType(String pattern) {
		this.fmt = DateTimeFormatter.ofPattern(pattern);
	}
	
	public String format(LocalDateTime loadDate) {
		if (loadDate == null) {
			return null;
		}
		return loadDate.format(fmt);
	}
	
}
