package com.iwellmass.idc.app.vo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutionRequest  {

	private String jobId;
	
	private LocalDateTime shouldFireTime;
	
	private String jobParameter;

	public LocalDateTime getShouldFireTime() {
		return shouldFireTime;
	}

	public void setShouldFireTime(LocalDateTime shouldFireTime) {
		this.shouldFireTime = shouldFireTime;
	}

	public String getJobParameter() {
		return jobParameter;
	}

	public void setJobParameter(String jobParameter) {
		this.jobParameter = jobParameter;
	}
	
	/*public String getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(String loadDate) {
		this.loadDate = loadDate;
	}
	
	public String getJobParameter() {
		return jobParameter;
	}

	public void setJobParameter(String jobParameter) {
		this.jobParameter = jobParameter;
	}*/
	
	

}
