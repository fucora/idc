package com.iwellmass.idc.quartz;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IDCPluginConfig {
	
	private String version;
	
	private Timestamp configVersion;

	private Integer schedulerParallelMax = 5;
	
	private boolean barrierClearOnStartup = true;

	
}
