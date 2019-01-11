package com.iwellmass.idc.quartz;

import java.util.List;

import com.iwellmass.idc.model.JobKey;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GuardEnv {
	
	private Integer instanceId;
	
	private Long shouldFireTime;
	
	private List<JobKey> barrierKeys;

}
