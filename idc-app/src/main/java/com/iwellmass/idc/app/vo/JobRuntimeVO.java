package com.iwellmass.idc.app.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.scheduler.model.JobState;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class JobRuntimeVO {
	
	@ApiModelProperty("ID")
	private String id;
	
	@ApiModelProperty("任务名称")
	private String taskName; // name
	
	@ApiModelProperty("开始时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime starttime;
	
	@ApiModelProperty("结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime updatetime;
	
	@ApiModelProperty("业务日期")
	private String loadDate;
	
	@ApiModelProperty("JOB状态")
	private JobState state;

	@ApiModelProperty("执行批次")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime shouldFireTime;

	@ApiModelProperty("执行方式")
	private ScheduleType scheduleType;

	@ApiModelProperty("批次时间")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private LocalDate batchTime;
}
