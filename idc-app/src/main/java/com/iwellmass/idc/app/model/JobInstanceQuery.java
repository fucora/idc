package com.iwellmass.idc.app.model;

import com.iwellmass.common.criteria.Between;
import com.iwellmass.common.criteria.BetweenPair;
import com.iwellmass.common.criteria.CustomCriteria;
import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.idc.app.vo.IdOrNameCriteria;
import com.iwellmass.idc.app.vo.YMDHMSBetweenPair;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobInstanceQuery implements SpecificationBuilder {

	@ApiModelProperty("任务名")
	@CustomCriteria(builder = IdOrNameCriteria.class)
	private String jobName;
	
	@ApiModelProperty("执行批次")
	@Between(value = "shouldFireTime")
	private BetweenPair<Long> shouldFireTime;

	@ApiModelProperty("开始时间")
	@Between(value = "startTime")
	private YMDHMSBetweenPair startTime;
}
