package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

public enum JobInstanceType {
	
	@ApiModelProperty("周期实例")
	CRON,
	
	@ApiModelProperty("手动实例")
	MANUAL,
	
	@ApiModelProperty("补数实例")
	COMPLEMENT;
}
