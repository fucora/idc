package com.iwellmass.idc.app.model;

import com.iwellmass.idc.model.TaskKey;

public class ExecutionRequest extends TaskKey {

	private static final long serialVersionUID = -8693972886473180967L;

	private String loadDate;
	
	private String jobParameter;
	
	public String getLoadDate() {
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
	}

}
