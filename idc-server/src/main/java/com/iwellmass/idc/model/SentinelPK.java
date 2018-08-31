package com.iwellmass.idc.model;

import java.io.Serializable;

public class SentinelPK implements Serializable {

	private static final long serialVersionUID = 7546711538636604222L;

	private String triggerName;

	private String triggerGroup;

	private Long shouldFireTime;

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerGroup() {
		return triggerGroup;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	public Long getShouldFireTime() {
		return shouldFireTime;
	}

	public void setShouldFireTime(Long shouldFireTime) {
		this.shouldFireTime = shouldFireTime;
	}

}
