package com.iwellmass.idc.app.vo;

import java.time.LocalDateTime;

import com.iwellmass.idc.scheduler.model.JobState;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRuntimeVO {
	
	@ApiModelProperty("ID")
	private String id;
	
	@ApiModelProperty("任务名称")
	private String name;
	
	@ApiModelProperty("开始时间")
	private LocalDateTime starttime;
	
	@ApiModelProperty("更新时间")
	private LocalDateTime updatetime;
	
	@ApiModelProperty("业务日期")
	private String loadDate;
	
	@ApiModelProperty("JOB状态")
	JobState state;
}
