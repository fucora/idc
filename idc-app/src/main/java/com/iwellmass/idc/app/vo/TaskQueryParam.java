package com.iwellmass.idc.app.vo;

import com.iwellmass.common.criteria.CustomCriteria;
import com.iwellmass.common.criteria.Equal;
import com.iwellmass.common.criteria.Like;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.scheduler.model.ScheduleType;
import com.iwellmass.idc.scheduler.model.TaskType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 调度计划列表查询参数
 */
@Getter
@Setter
public class TaskQueryParam extends Pager {

	@ApiModelProperty("计划名称或者工作流id")
	@CustomCriteria(builder = CustomCriteriaBuilder.class)
	private String keyword;

	@ApiModelProperty("节点类型")
	@Equal
	private TaskType taskType;

	@ApiModelProperty("业务类型")
	@Equal
	private String contentType;

	@ApiModelProperty("调度类型")
	@Equal
	private ScheduleType scheduleType;

	@ApiModelProperty("负责人")
	@Equal
	private String assignee;

	public TaskQueryParam() {
		setPage(0);
		setLimit(10);
	}
}
