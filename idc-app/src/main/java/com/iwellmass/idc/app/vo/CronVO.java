package com.iwellmass.idc.app.vo;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronVO {
	
	@ApiModelProperty("哪一天")
	private List<Integer> days;

	@ApiModelProperty("具体时间")
	@JsonFormat(timezone = "GMT+8", pattern="HH:mm:ss")
	private LocalTime duetime = LocalTime.MIN;
}
