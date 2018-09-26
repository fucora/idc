package com.iwellmass.idc.model;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class ScheduleProperties {

	@ApiModelProperty("哪一天")
	private List<Integer> days;

	@ApiModelProperty("具体时间")
	private String duetime = "00:00:00";

	@ApiModelProperty("调度类型")
	private ScheduleType scheduleType;
	

	public List<Integer> getDays() {
		return days;
	}

	public void setDays(List<Integer> days) {
		this.days = days;
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
