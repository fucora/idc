package com.iwellmass.idc.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowVO {
	
	@ApiModelProperty("id")
	private String id;

	@ApiModelProperty("名称")
	private String workflowName;
	
	@ApiModelProperty("描述")
	private String description;

	@ApiModelProperty("是否可以编辑")
	private boolean canModify = false;

	@ApiModelProperty("是否可以删除")
	private boolean canDelete = false;
}
