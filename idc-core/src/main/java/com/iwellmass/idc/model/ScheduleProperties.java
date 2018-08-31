package com.iwellmass.idc.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class ScheduleProperties {

	@ApiModelProperty("每月号数 ( 0 ~ 30 )")
	private List<Integer> daysOfMonth;

	@ApiModelProperty("每周星期数  1(周日) ~ 7(周六) ) ")
	private List<Integer> daysOfWeek;

	@ApiModelProperty("具体时间")
	private String duetime = "00:00:00";

	@ApiModelProperty("调度类型")
	private ScheduleType scheduleType;

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

	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}

}
