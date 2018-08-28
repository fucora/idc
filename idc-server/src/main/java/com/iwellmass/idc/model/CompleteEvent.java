package com.iwellmass.idc.model;

import java.time.LocalDateTime;

public class CompleteEvent extends JobStatusEvent {

	private LocalDateTime endTime;
	
	
	private JobInstanceStatus finalStatus;

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public JobInstanceStatus getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(JobInstanceStatus finalStatus) {
		this.finalStatus = finalStatus;
	}

}
