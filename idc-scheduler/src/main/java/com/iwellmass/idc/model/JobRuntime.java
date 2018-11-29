package com.iwellmass.idc.model;

import lombok.Data;

@Data
public class JobRuntime {

	private Integer instanceId;
	
	private JobKey jobKey;
	
	private TaskKey taskKey;
	
	private Long shouldFireTime;
	
	private Long prevFireTime;
	
	private String parameter;

	private ScheduleType scheduleType;
	
	private String assignee;
	
	private Integer workflowInstanceId;
	
}
