package com.iwellmass.idc.app.vo;

import com.iwellmass.common.criteria.Like;
import com.iwellmass.common.util.Pager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowQueryParam extends Pager {

	@ApiModelProperty("关键字")
	@Like(value = "taskName")
	private String keyword;
}
