package com.iwellmass.idc.app.vo;

import com.iwellmass.common.criteria.Equal;
import com.iwellmass.common.criteria.Like;
import com.iwellmass.common.util.Pager;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobQueryParam extends Pager{

	@ApiModelProperty("关键字")
	@Like(value = "name")
	String keyword;
	
	@ApiModelProperty("指定任务")
	@Equal(value = "name")
	String taskName;
}
