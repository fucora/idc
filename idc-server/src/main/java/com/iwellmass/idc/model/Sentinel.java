package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.iwellmass.idc.quartz.IDCPlugin;

@Entity
@IdClass(SentinelPK.class)
@Table(name = "t_idc_sentinel")
public class Sentinel {

	private String triggerName;

	private String triggerGroup;

	private Long shouldFireTime;

	private SentinelStatus status;

	@Id
	@Column(name = IDCPlugin.COL_SENTINEL_TRIGGER_NAME, length = 50)
	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	@Id
	@Column(name = IDCPlugin.COL_SENTINEL_TRIGGER_GROUP, length = 50)
	public String getTriggerGroup() {
		return triggerGroup;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	@Id
	@Column(name = IDCPlugin.COL_SENTINEL_SHOULD_FIRE_TIME)
	public Long getShouldFireTime() {
		return shouldFireTime;
	}

	public void setShouldFireTime(Long loadDate) {
		this.shouldFireTime = loadDate;
	}

	@Column(name = IDCPlugin.COL_SENTINEL_STATUS)
	public SentinelStatus getStatus() {
		return status;
	}

	public void setStatus(SentinelStatus status) {
		this.status = status;
	}

}
