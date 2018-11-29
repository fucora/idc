package com.iwellmass.idc.app.model;

import java.time.LocalDateTime;

import com.iwellmass.idc.model.JobKey;

public class ExecutionRequest extends JobKey {

	private static final long serialVersionUID = -8693972886473180967L;

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
