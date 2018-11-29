package com.iwellmass.idc.app.vo;

import java.util.List;

import com.iwellmass.idc.model.ScheduleStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRuntime {
	
	private ScheduleStatus status;
	
	private List<JobBarrierVO> barriers;
	
}
