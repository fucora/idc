package com.iwellmass.idc.scheduler.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * 调度状态
 */
public enum TaskState {

	@ApiModelProperty("等待调度")
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
