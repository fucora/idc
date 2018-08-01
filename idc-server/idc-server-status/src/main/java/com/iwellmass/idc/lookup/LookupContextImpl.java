package com.iwellmass.idc.lookup;

import java.time.LocalDateTime;

public class LookupContextImpl implements LookupContext {
	
	
	private String jobId;
	
	private String jobParameter;
	
	private String loadDate;

	@Override
	public String jobId() {
		return null;
	}

	@Override
	public String jobParameter() {
		return null;
	}

	@Override
	public LocalDateTime loadDate() {
		return null;
	}

	@Override
	public void fireSourceEvent(SourceEvent event) {
		System.out.println(event);
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobParameter() {
		return jobParameter;
	}

	public void setJobParameter(String jobParameter) {
		this.jobParameter = jobParameter;
	}

	public String getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(String loadDate) {
		this.loadDate = loadDate;
	}

}
