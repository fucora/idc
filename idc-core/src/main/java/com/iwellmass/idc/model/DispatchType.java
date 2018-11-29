package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

public enum DispatchType {

	@ApiModelProperty("周期实例")
	AUTO, 
	
	@ApiModelProperty("手动实例")
	MANUAL,

	@ApiModelProperty("补数实例")
	COMPLEMENT;

}
