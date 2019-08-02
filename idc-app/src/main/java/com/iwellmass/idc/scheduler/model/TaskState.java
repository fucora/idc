package com.iwellmass.idc.scheduler.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * 调度状态
 */
public enum TaskState {

	@ApiModelProperty("等待执行")
	WAITING,
	@ApiModelProperty("冻结")
	PAUSED,
	@ApiModelProperty("准备执行")
	ACQUIRED,
	@ApiModelProperty("执行阻塞")
	BLOCKED,
	@ApiModelProperty("冻结")
	SUSPENDED,
	@ApiModelProperty("执行异常")
	ERROR,
	@ApiModelProperty("未执行")
	NONE,
	@ApiModelProperty("正在执行")
	NORMAL,
	@ApiModelProperty("执行完成")
	COMPLETE;
	public boolean isTerminated() {
		return this==COMPLETE||this==ERROR;
	}

	public String desc() {
		return name();
	}
}
