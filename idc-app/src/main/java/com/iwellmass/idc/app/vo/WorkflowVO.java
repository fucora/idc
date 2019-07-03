package com.iwellmass.idc.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowVO {
	
	@ApiModelProperty
	private String id;

	@ApiModelProperty
	private String name;
	
	@ApiModelProperty
	private String description;
	
}
