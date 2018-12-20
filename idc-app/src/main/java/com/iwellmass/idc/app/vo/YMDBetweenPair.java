package com.iwellmass.idc.app.vo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.common.criteria.BetweenPair;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YMDBetweenPair extends BetweenPair<LocalDate>{

	@ApiModelProperty("开始 yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	private LocalDate from;
	
	@ApiModelProperty("截止 yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	private LocalDate to;
}
