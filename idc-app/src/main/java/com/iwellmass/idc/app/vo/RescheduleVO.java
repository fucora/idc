package com.iwellmass.idc.app.vo;

import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.ScheduleProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RescheduleVO extends JobKey{

	private static final long serialVersionUID = 3130073773275611705L;
	
	private ScheduleProperties scheduleConfig;
	
}
