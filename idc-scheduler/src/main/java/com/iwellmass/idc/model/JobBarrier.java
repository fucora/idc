package com.iwellmass.idc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_idc_barrier")
public class JobBarrier implements Serializable {

	private static final long serialVersionUID = 3891057699732617536L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;

	@Column(name = "job_id")
	private String jobId;

	@Column(name = "job_group")
	private String jobGroup;
	
	@Column(name = "job_name")
	private String jobName;

	@Column(name = "barrier_id")
	private String barrierId;

	@Column(name = "barrier_group")
	private String barrierGroup;
	
	@Column(name = "barrier_name")
	private String barrierName;

	@Column(name = "barrier_should_fire_time")
	private Long barrierShouldFireTime;
	
	@Column(name = "state")
	private BarrierState state;
	
	public void setBarrierKey(JobKey barrierKey) {
		this.barrierId = barrierKey.getJobId();
		this.barrierGroup = barrierKey.getJobGroup();
	}
	
	public void setJobKey(JobKey jobKey) {
		this.jobId = jobKey.getJobId();
		this.jobGroup = jobKey.getJobGroup();
	}
}