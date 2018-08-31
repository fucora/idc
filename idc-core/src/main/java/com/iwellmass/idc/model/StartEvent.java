package com.iwellmass.idc.model;

import java.time.LocalDateTime;

public class StartEvent extends JobStatusEvent{
	
	private LocalDateTime startTime;

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
}
