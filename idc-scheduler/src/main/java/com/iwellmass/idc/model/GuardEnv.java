package com.iwellmass.idc.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GuardEnv {
	
	private Integer instanceId;
	
	private Long shouldFireTime;
	
	private List<JobKey> barrierKeys;

}
