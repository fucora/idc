package com.iwellmass.idc.app.model;

import java.util.List;

import com.iwellmass.idc.app.vo.JobBarrierVO;
import com.iwellmass.idc.model.ScheduleStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRuntime {
	
	private Integer instanceId;
	
	private ScheduleStatus status;
	
	private List<JobBarrierVO> barriers;
	
}
