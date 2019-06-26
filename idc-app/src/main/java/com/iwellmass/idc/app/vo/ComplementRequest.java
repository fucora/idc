package com.iwellmass.idc.app.vo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplementRequest {

	private String id;

	@JsonFormat(pattern = "yyyyMMdd")
	@ApiModelProperty("开始时间，yyyyMMdd")
	private LocalDate startTime;

	@JsonFormat(pattern = "yyyyMMdd")
	@ApiModelProperty("截至时间，yyyyMMdd")
	private LocalDate endTime;
}
