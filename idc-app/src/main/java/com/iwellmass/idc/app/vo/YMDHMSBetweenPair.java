package com.iwellmass.idc.app.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.common.criteria.BetweenPair;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YMDHMSBetweenPair extends BetweenPair<LocalDateTime>{

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime from;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime to;
}
