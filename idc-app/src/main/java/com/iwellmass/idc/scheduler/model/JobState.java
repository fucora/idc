package com.iwellmass.idc.scheduler.model;

import io.swagger.annotations.ApiModelProperty;

// copy-of-YARN :) NEW, NEW_SAVING, SUBMITTED, ACCEPTED, RUNNING, FINISHED, FAILED, KILLED
public enum JobState {
	
	@ApiModelProperty("未调度")
	NONE,
	@ApiModelProperty("等待派发")
	NEW,
	@ApiModelProperty("已派发")
	ACCEPTED,
	@ApiModelProperty("运行中")
	RUNNING,
	@ApiModelProperty("成功")
	FINISHED,
	@ApiModelProperty("跳过")
	SKIPPED,
	@ApiModelProperty("失败")
	FAILED,
	@ApiModelProperty("取消")
	CANCEL,
	@ApiModelProperty("暂停")
	PAUSED;
	
	public boolean isComplete() {
		return isSuccess() || isFailure();
	}

	// 任务已完成
	public boolean isSuccess() {
		return this == FINISHED || this == JobState.SKIPPED;
	}
	
	// 任务有错误
	public boolean isFailure() {
		return this == FAILED || this == JobState.CANCEL;
	}

	public String desc() {
		return null;
	}

	public boolean isRunning() {
		return this == ACCEPTED || this == RUNNING;
	}

	public boolean isPaused() {
		return this == PAUSED;
	}

	public boolean isNone() {
		return this == NONE;
	}
}
