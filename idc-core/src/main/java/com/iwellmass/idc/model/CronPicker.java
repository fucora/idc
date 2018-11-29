package com.iwellmass.idc.model;

import java.time.LocalTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronPicker {
	
	private List<Integer> days;
	private LocalTime duetime;
	
}
