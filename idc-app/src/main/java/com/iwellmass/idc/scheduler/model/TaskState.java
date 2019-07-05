package com.iwellmass.idc.scheduler.model;

/**
 * 调度状态
 */
public enum TaskState {

	NORMAL;

	public boolean isTerminated() {
		return true;
	}

	public String desc() {
		return name();
	}
}
