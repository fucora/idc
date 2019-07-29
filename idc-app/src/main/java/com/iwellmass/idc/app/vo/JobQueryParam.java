package com.iwellmass.idc.app.vo;

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
	@Like
	private String taskName;

	@ApiModelProperty("job的类型")
	@Equal
	private JobType jobType;

	@ApiModelProperty("开启时间")
	@Between(to = "updatetime")
	private LocalDateTime starttime;

	@ApiModelProperty("结束时间")
	private LocalDateTime updatetime;
}
