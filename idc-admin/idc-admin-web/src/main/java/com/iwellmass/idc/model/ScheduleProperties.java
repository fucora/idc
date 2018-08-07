package com.iwellmass.idc.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;

import io.swagger.annotations.ApiModelProperty;

public class ScheduleProperties {

	@ApiModelProperty("每月号数 ( 0 ~ 30 )")
	private List<Integer> daysOfMonth;

	@ApiModelProperty("每周星期数  1(周日) ~ 7(周六) ) ")
	private List<Integer> daysOfWeek;

	@ApiModelProperty("具体时间")
	private String duetime = "00:00:00";

	public List<Integer> getDaysOfMonth() {
		return daysOfMonth;
	}

	public void setDaysOfMonth(List<Integer> daysOfMonth) {
		this.daysOfMonth = daysOfMonth;
	}

	public List<Integer> getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(List<Integer> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	public String getDuetime() {
		return duetime;
	}

	public void setDuetime(String duetime) {
		this.duetime = duetime;
	}

	public String toCronExpr(ScheduleType type) {
		
		LocalTime duetime = LocalTime.parse(this.duetime, DateTimeFormatter.ISO_TIME);
		switch (type) {
		case MONTHLY:
			Assert.isFalse(Utils.isNullOrEmpty(daysOfMonth), "月调度配置不能为空");
			return String.format("%s %s %s %s * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(), 
					String.join(",", daysOfMonth.stream().map(i -> i+ "").collect(Collectors.toList())));
		case WEEKLY:
			Assert.isFalse(Utils.isNullOrEmpty(daysOfMonth), "周调度配置不能为空");
			return String.format("%s %s %s ? * %s *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(), 
					String.join(",", daysOfWeek.stream().map(i -> i+ "").collect(Collectors.toList())));
		case DAILY:
			return String.format("%s %s %s * * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour());
		default:
			throw new UnsupportedOperationException("not supported yet.");
		}
	}
	
}
