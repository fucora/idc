package com.iwellmass.idc.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class JobKey implements Serializable {

	private static final long serialVersionUID = -6687330393375678068L;

	private String jobId;

	private String jobGroup;

	public JobKey() {}

	public JobKey(String jobId, String jobGroup) {
		super();
		this.jobId = jobId;
		this.jobGroup = jobGroup;
	}

	@Override
	public String toString() {
		return jobGroup + "." + jobId;
	}

}
