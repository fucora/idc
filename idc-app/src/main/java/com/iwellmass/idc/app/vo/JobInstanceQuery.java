package com.iwellmass.idc.app.vo;

import com.iwellmass.common.criteria.Between;
import com.iwellmass.common.criteria.CustomCriteria;
import com.iwellmass.common.criteria.Equal;
import com.iwellmass.common.criteria.LocalDateLongConverter;
import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.idc.model.DispatchType;

import io.swagger.annotations.ApiModelProperty;
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
	
	@Equal
	private DispatchType dispatchType;
	
	@ApiModelProperty("执行批次")
	@Between(value = "shouldFireTime", converter = LocalDateLongConverter.class)
	private YMDBetweenPair shouldFireTime;

	@ApiModelProperty("开始时间")
	@Between(value = "startTime")
	private YMDHMSBetweenPair startTime;

}
