package com.iwellmass.idc.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import io.swagger.annotations.ApiModelProperty;

/**
 * 周期类型
 */
public enum ScheduleType {

	@ApiModelProperty("月调度")
	MONTHLY("yyyyMMdd"),

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

	public LocalDateTime parse(String loadDateStr) {
		if (loadDateStr == null) {
			return null;
		}

		LocalTime min = LocalTime.of(0, 0, 0);

		if (this == ScheduleType.MONTHLY) {
			if (loadDateStr.length() == 6) {
				loadDateStr += "01";
			}
			return LocalDateTime.of(LocalDate.parse(loadDateStr, fmt).plusMonths(1).minusDays(1), min);

		} else if (this == DAILY) {
			return LocalDateTime.of(LocalDate.parse(loadDateStr, fmt), min);
		}
		return LocalDateTime.parse(loadDateStr, fmt);
	}

	public static void main(String[] args) {
		System.out.println(DAILY.parse("20180101"));
		System.out.println(MONTHLY.parse("201802"));
	}
}
