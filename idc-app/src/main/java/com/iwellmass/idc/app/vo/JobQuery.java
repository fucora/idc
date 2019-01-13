package com.iwellmass.idc.app.vo;

import com.iwellmass.common.criteria.Equal;
import com.iwellmass.common.criteria.Like;
import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.TaskType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobQuery implements SpecificationBuilder {

	@ApiModelProperty("计划名称")
	@Like
	private String jobName;

	@ApiModelProperty("节点类型")
	@Equal
	private TaskType taskType;
	
	@ApiModelProperty("业务类型")
	@Equal
	private String contentType;

	@ApiModelProperty("调度类型")
	@Equal
	private DispatchType dispatchType;

	@ApiModelProperty("负责人")
	@Equal
	private String assignee;

}
