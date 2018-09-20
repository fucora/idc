package com.iwellmass.idc.service;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.model.JobPK;

public class ExecutionRequest extends JobPK {

	private static final long serialVersionUID = -8693972886473180967L;

	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	private LocalDate loadDate;
	
	private String jobParameter;
	
	public LocalDate getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(LocalDate loadDate) {
		this.loadDate = loadDate;
	}

	public String getJobParameter() {
		return jobParameter;
	}

	public void setJobParameter(String jobParameter) {
		this.jobParameter = jobParameter;
	}

}
