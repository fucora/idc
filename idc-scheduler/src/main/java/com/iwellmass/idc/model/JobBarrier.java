package com.iwellmass.idc.model;

import static com.iwellmass.idc.quartz.IDCConstants.COL_BARRIER_STATE;
import static com.iwellmass.idc.quartz.IDCConstants.COL_BARRIER_GROUP;
import static com.iwellmass.idc.quartz.IDCConstants.COL_BARRIER_NAME;
import static com.iwellmass.idc.quartz.IDCConstants.COL_BARRIER_SHOULD_FIRE_TIME;
import static com.iwellmass.idc.quartz.IDCConstants.COL_IDC_JOB_GROUP;
import static com.iwellmass.idc.quartz.IDCConstants.COL_IDC_JOB_NAME;
import static com.iwellmass.idc.quartz.IDCConstants.TABLE_BARRIER;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
	private Long shouldFireTime;
	
	@Column(name = COL_BARRIER_STATE)
	private BarrierState state;
	
	public void setBarrierKey(JobKey barrierKey) {
		this.barrierId = barrierKey.getJobId();
		this.barrierGroup = barrierKey.getJobGroup();
	}
	
	public void setJobKey(JobKey jobKey) {
		this.jobId = jobKey.getJobId();
		this.jobGroup = jobKey.getJobGroup();
	}

	@Override
	public String toString() {
		return "JobBarrier [barrierId=" + barrierId + ", barrierGroup=" + barrierGroup + ", shouldFireTime=" + shouldFireTime + "]";
	}
	
}
