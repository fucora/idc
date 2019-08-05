package com.iwellmass.idc.app.vo;

import com.iwellmass.common.criteria.CustomCriteria;
import com.iwellmass.common.criteria.Like;
import com.iwellmass.common.util.Pager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowQueryParam extends Pager {

	@ApiModelProperty("关键字")
	@CustomCriteria(builder = WorkflowCustomCriteriaBuilder.class)
	private String keyword;
	
	public WorkflowQueryParam() {
		// 默认参数
		setPage(0);
		setLimit(10);
	}
}
