package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
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

	@ApiModelProperty("依赖的 jobId")
	@Column(name = "job_id", length = 50)
	private String jobId;

	@ApiModelProperty("依赖的 jobGroup")
	@Column(name = "job_group", length = 50)
	private String jobGroup;

}
