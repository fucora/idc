package com.iwellmass.idc.scheduler.model;

/**
 * 调度状态
 */
public enum TaskState {

	WAITING,
	PAUSED,
	ACQUIRED,
	BLOCKED,
	SUSPENDED,
	ERROR,
	NONE,
	NORMAL,
	COMPLETE;
	public boolean isTerminated() {
		return this==COMPLETE||this==ERROR;
	}

	public String desc() {
		return name();
	}
}
