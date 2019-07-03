package com.iwellmass.idc.app.vo;

import com.iwellmass.common.util.Pager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowQueryParam extends Pager {

	@ApiModelProperty("关键字")
	private String keyword;
}
