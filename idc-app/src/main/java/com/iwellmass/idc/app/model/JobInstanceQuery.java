package com.iwellmass.idc.app.model;

import com.iwellmass.common.criteria.Between;
import com.iwellmass.common.criteria.CustomCriteria;
import com.iwellmass.common.criteria.Equal;
import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.idc.app.vo.IdOrNameCriteria;
import com.iwellmass.idc.app.vo.YMDHMSBetweenPair;

import com.iwellmass.idc.model.DispatchType;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobInstanceQuery implements SpecificationBuilder {
	
	@Equal
	private String jobId;
	
	@Equal
	private String jobGroup;
	
	@ApiModelProperty("任务名")
	@CustomCriteria(builder = IdOrNameCriteria.class)
	private String jobName;
	
	@ApiModelProperty("执行批次")
	@Between(value = "shouldFireTime")
	private YMDHMSBetweenPair shouldFireTime;

	@ApiModelProperty("开始时间")
	@Between(value = "startTime")
	private YMDHMSBetweenPair startTime;

	@ApiModelProperty("派发类型")
	@Equal
	private DispatchType dispatchType;
}
