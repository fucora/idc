package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name = "t_idc_dependency")
public class JobDependency {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "src_job_id", length = 50)
	private String srcJobId;

	@Column(name = "src_job_group", length = 50)
	private String srcJobGroup;

	@Column(name = "job_id")
	private String jobId;

	@Column(name = "job_group")
	private String jobGroup;

	@Transient
	public JobKey getSrcJobKey() {
		return new JobKey(srcJobId, srcJobGroup);
	}

	@Transient
	public JobKey getDependencyJobKey() {
		return new JobKey(jobId, jobGroup);
	}
}
