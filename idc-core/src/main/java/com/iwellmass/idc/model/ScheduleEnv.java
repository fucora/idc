package com.iwellmass.idc.model;

import lombok.Data;

@Data
public class ScheduleEnv {
	
	private Integer instanceId;
	
	private Long shouldFireTime;
	
	private String parameter;
	
}
