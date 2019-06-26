package com.iwellmass.idc.app.scheduler;

import java.sql.Timestamp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IDCPluginConfig {
	
	private String version;
	
	private Timestamp configVersion;

	@ApiModelProperty("最大并发任务数")
	private Integer schedulerParallelMax = 5;
	
	private boolean barrierClearOnStartup = true;

	
}
