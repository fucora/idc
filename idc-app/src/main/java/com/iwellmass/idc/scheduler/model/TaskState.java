package com.iwellmass.idc.scheduler.model;

/**
 * 调度状态
 */
public enum TaskState {

//	WAITING,
//	PAUSED,
//	ACQUIRED,
//	BLOCKED,
//	ERROR
	NONE,
	NORMAL,
	PAUSED,
	COMPLETE,
	ERROR,
	BLOCKED;

	public boolean isTerminated() {
		return this==COMPLETE||this==ERROR;
	}

	public String desc() {
		return name();
	}
}
