package com.iwellmass.idc.model;

import java.io.Serializable;

public class JobPK implements Serializable {

	private static final long serialVersionUID = -6687330393375678068L;

	private String jobId;

	private String jobGroup;

	public JobPK() {
	}

	public JobPK(String jobId, String jobGroup) {
		super();
		this.jobId = jobId;
		this.jobGroup = jobGroup;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	
	@Override
	public String toString() {
		return jobGroup + "." + jobId;
	}

}
