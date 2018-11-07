package com.iwellmass.idc.model;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 调度配置
 */
@Getter
@Setter
public class ScheduleProperties {

	@ApiModelProperty("哪一天")
	private List<Integer> days;

	@ApiModelProperty("具体时间")
	private String duetime = "00:00:00";

	@ApiModelProperty("调度类型")
	private ScheduleType scheduleType;
	
	@ApiModelProperty("失败重试")
	private Boolean isRetry = true;
	
	@ApiModelProperty("出错时阻塞")
	private Boolean blockOnError = true;
	
	@ApiModelProperty("生效日期始 yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	@ApiModelProperty("生效日期止, yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;

}
