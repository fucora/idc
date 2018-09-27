package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.swagger.annotations.ApiOperation;

@Entity
@Table(name = "t_idc_dependency")
@IdClass(JobDependencyPK.class)
public class JobDependency {
	
	private String srcJobId;
	
	private String srcJobGroup;

	private String jobId;

	private String jobGroup;

	@Id
	@Column(name = "src_job_id", length = 50)
	public String getSrcJobId() {
		return srcJobId;
	}

	public void setSrcJobId(String srcJobId) {
		this.srcJobId = srcJobId;
	}

	@Id
	@Column(name = "src_job_group", length = 50)
	public String getSrcJobGroup() {
		return srcJobGroup;
	}

	public void setSrcJobGroup(String srcJobGroup) {
		this.srcJobGroup = srcJobGroup;
	}

	@Id
	@Column(name = "job_id", length = 50)
	@ApiOperation("依赖的 taskId")
	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	@Id
	@Column(name = "job_group", length = 50)
	@ApiOperation("依赖的 groupId")
	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String srcGroup) {
		this.jobGroup = srcGroup;
	}

}
