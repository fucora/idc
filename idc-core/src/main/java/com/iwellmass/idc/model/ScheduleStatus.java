package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

// copy-of-quartz TriggerState
public enum ScheduleStatus {

	@ApiModelProperty("等待调度")
	NONE,

	@ApiModelProperty("正常")
	NORMAL,

	@ApiModelProperty("冻结")
	PAUSED,

	@ApiModelProperty("完结")
	COMPLETE,

	@ApiModelProperty("调度异常")
	ERROR,

	@ApiModelProperty("调度阻塞")
	BLOCKED;

	public boolean isComplete() {
		return this == COMPLETE;
	}
}
