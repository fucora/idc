package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

public enum JobStatus {

	@ApiModelProperty("冻结")
	LOCK,
	@ApiModelProperty("未冻结")
	UNLOCK
}
