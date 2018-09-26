package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("任务实例类型")
public enum JobInstanceType {
	
	@ApiModelProperty("周期实例")
	AUTO,
	
	@ApiModelProperty("手动实例")
	MANUAL,
	
	@ApiModelProperty("补数实例")
	COMPLEMENT;
}
