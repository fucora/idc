package com.iwellmass.idc.app.vo;

import java.time.LocalDateTime;

import com.iwellmass.idc.model.JobInstanceStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobBarrierVO {

	@ApiModelProperty("任务ID")
	private String barrierId;
	
	@ApiModelProperty("任务组")
	private String barrierGroup;
	
	@ApiModelProperty("任务名称")
	private String barrierName;
	
	@ApiModelProperty("任务名称")
	private JobInstanceStatus barrierStatus = JobInstanceStatus.NONE;
	
	@ApiModelProperty("执行时间")
	private LocalDateTime shouldFireTime;
}
