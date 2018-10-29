package com.iwellmass.idc.model;

import static com.iwellmass.idc.quartz.IDCConstants.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = TABLE_BARRIER)
public class JobBarrier implements Serializable {

	private static final long serialVersionUID = 3891057699732617536L;

	@Id
	@Column(name = "ID")
	private Integer id;

	@Column(name = COL_IDC_JOB_NAME)
	private String jobId;

	@Column(name = COL_IDC_JOB_GROUP)
	private String jobGroup;

	@Column(name = COL_BARRIER_NAME)
	private String barrierId;

	@Column(name = COL_BARRIER_GROUP)
	private String barrierGroup;

	@Column(name = COL_BARRIER_SHOULD_FIRE_TIME)
	private LocalDateTime barrierLoadDate;

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

	public String getBarrierId() {
		return barrierId;
	}

	public void setBarrierId(String barrierId) {
		this.barrierId = barrierId;
	}

	public String getBarrierGroup() {
		return barrierGroup;
	}

	public void setBarrierGroup(String barrierGroup) {
		this.barrierGroup = barrierGroup;
	}
	
	public LocalDateTime getBarrierLoadDate() {
		return barrierLoadDate;
	}

	public void setBarrierLoadDate(LocalDateTime barrierLoadDate) {
		this.barrierLoadDate = barrierLoadDate;
	}

	@Override
	public String toString() {
		return "JobBarrier [barrierId=" + barrierId + ", barrierGroup=" + barrierGroup + ", loadDate=" + barrierLoadDate + "]";
	}
	
}
