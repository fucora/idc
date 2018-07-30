package com.iwellmass.idc.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "t_idc_dependency")
public class JobDependency {

	private int id;

	private int jobId;

	public String jobName;
	
	private int dependencyId;
	
	private String dependencyName;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ApiModelProperty("任务ID")
	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	
	@ApiModelProperty("任务名称")
	@Transient
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@ApiModelProperty("依赖任务ID")
	public int getDependencyId() {
		return dependencyId;
	}

	public void setDependencyId(int dependencyId) {
		this.dependencyId = dependencyId;
	}

	@ApiModelProperty("依赖任务名称")
	@Transient
	public String getDependencyName() {
		return dependencyName;
	}

	public void setDependencyName(String dependencyName) {
		this.dependencyName = dependencyName;
	}

}
