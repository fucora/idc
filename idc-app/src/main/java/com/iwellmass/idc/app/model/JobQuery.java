package com.iwellmass.idc.app.model;

import java.util.List;

import com.iwellmass.common.criteria.Equal;
import com.iwellmass.common.criteria.In;
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

	@ApiModelProperty("任务名")
	@Like
	private String taskName;

	@ApiModelProperty("任务类型")
	@Equal
	private String contentType;

	@ApiModelProperty("节点类型")
	@In("taskType")
	private List<TaskType> taskTypes;

	@ApiModelProperty("调度类型类型")
	@Equal
	private DispatchType dispatchType;

	@ApiModelProperty("负责人")
	@Equal
	private String assignee;

}
