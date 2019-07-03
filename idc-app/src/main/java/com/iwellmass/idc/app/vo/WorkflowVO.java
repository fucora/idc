package com.iwellmass.idc.app.vo;

import com.iwellmass.idc.app.vo.graph.GraphVO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowVO {
	
	@ApiModelProperty("id")
	private String id;

	@ApiModelProperty("名称")
	private String taskName;
	
	@ApiModelProperty("描述")
	private String description;
	
	@ApiModelProperty("图形关系")
	private GraphVO graph;
	
}
