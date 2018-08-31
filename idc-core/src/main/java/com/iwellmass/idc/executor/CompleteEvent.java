package com.iwellmass.idc.executor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.iwellmass.idc.model.JobInstanceStatus;

public class CompleteEvent extends JobInstanceEvent implements Serializable{
	
	private static final long serialVersionUID = -2050270529918044581L;

	public LocalDateTime getEndTime() {
		return null;
	}

	public JobInstanceStatus getFinalStatus() {
		return null;
	}

}
