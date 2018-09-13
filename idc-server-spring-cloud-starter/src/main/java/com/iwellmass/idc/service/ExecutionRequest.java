package com.iwellmass.idc.service;

import com.iwellmass.idc.model.JobPK;

public class ExecutionRequest extends JobPK {

	private static final long serialVersionUID = -8693972886473180967L;

	private String jobParameter;

	public String getJobParameter() {
		return jobParameter;
	}

	public void setJobParameter(String jobParameter) {
		this.jobParameter = jobParameter;
	}

}
