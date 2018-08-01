package com.iwellmass.idc.lookup;

import java.time.LocalDateTime;

public class SourceEvent {

	private String jobId;
	
	private LocalDateTime loadDate;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String orgNo) {
		this.jobId = orgNo;
	}

	public LocalDateTime getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(LocalDateTime loadDate) {
		this.loadDate = loadDate;
	}

	@Override
	public String toString() {
		return "SourceEvent [jobId=" + jobId + ", loadDate=" + loadDate + "]";
	}
	
}
