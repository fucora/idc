package com.iwellmass.idc.scheduler.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * 调度状态
 */
public enum TaskState {

	@ApiModelProperty("等待执行")
	WAITING,
	@ApiModelProperty("暂停")
	PAUSED,
	@ApiModelProperty("准备执行")
	ACQUIRED,
	@ApiModelProperty("执行阻塞")
	BLOCKED,
	@ApiModelProperty("扩展状态,阻断作用,实际是正在运行")
	SUSPENDED,
	@ApiModelProperty("暂停")
	PAUSED_SUSPENDED,
	@ApiModelProperty("执行异常")
	ERROR,
	@ApiModelProperty("未执行")
	NONE,
	@ApiModelProperty("正在执行")
	NORMAL,
	@ApiModelProperty("执行完成")
	COMPLETE,
	@ApiModelProperty("已取消")
	CANCEL;
	public boolean isTerminated() {
		return this==COMPLETE||this==ERROR;
	}

	public boolean isComplete() {
		return this == COMPLETE;
	}

	public String desc() {
		return name();
	}
}
