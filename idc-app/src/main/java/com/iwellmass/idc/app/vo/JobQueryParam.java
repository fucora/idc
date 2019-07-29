package com.iwellmass.idc.app.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.common.criteria.Between;
import com.iwellmass.common.criteria.Equal;
import com.iwellmass.common.criteria.Like;
import com.iwellmass.common.util.Pager;

import com.iwellmass.idc.scheduler.model.JobType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobQueryParam extends Pager{

	@ApiModelProperty("指定任务")
	@Like
	private String taskName;

	@ApiModelProperty("job的类型")
	@Equal
	private JobType jobType;

	@ApiModelProperty("时间区间筛选")
	@Between
	private YMDHMSBetweenPair ymdhmsBetweenPair;
}
