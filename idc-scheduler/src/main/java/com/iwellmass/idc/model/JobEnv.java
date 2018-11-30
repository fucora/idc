package com.iwellmass.idc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobEnv extends Job{

	private TaskKey taskKey;
	
	private JobKey jobKey;
	
	private Integer instanceId;
	
	private Integer mainInstanceId;

	private Long shouldFireTime;
	
	private Long prevFireTime;
	
}
