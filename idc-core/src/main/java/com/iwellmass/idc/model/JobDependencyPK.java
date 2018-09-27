package com.iwellmass.idc.model;

import java.io.Serializable;

public class JobDependencyPK implements Serializable {

	private static final long serialVersionUID = -4479061532661305224L;

	private String srcJobId;

	private String srcJobGroup;

	private String jobId;

	private String jobGroup;

	public String getSrcJobId() {
		return srcJobId;
	}

	public void setSrcJobId(String srcJobId) {
		this.srcJobId = srcJobId;
	}

	public String getSrcJobGroup() {
		return srcJobGroup;
	}

	public void setSrcJobGroup(String srcJobGroup) {
		this.srcJobGroup = srcJobGroup;
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

}
