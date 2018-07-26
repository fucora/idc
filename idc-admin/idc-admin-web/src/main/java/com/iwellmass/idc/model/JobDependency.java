package com.iwellmass.idc.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "t_idc_dependency")
public class JobDependency {

	private int id;

	private int jobId;

	private int dependencyId;

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

	@ApiModelProperty("依赖任务ID")
	public int getDependencyId() {
		return dependencyId;
	}

	public void setDependencyId(int dependencyId) {
		this.dependencyId = dependencyId;
	}

}
