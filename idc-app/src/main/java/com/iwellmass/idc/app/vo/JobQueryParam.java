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

import java.time.LocalDateTime;

@Getter
@Setter
public class JobQueryParam extends Pager{

	@ApiModelProperty("指定任务")
	@Equal
	private String taskName;

	@ApiModelProperty("job的类型")
	@Equal
	private JobType jobType;

	@ApiModelProperty("时间区间筛选")
	@Between(value = "starttime")
	private YMDHMSBetweenPair ymdhmsBetweenPair;

	// old
//	@Between(value = "starttime",to = "updatetime")
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//	private LocalDateTime starttime;
//
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//	private LocalDateTime updatetime;
}
