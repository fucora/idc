package com.iwellmass.idc.scheduler.model;

// copy-of-YARN :) NEW, NEW_SAVING, SUBMITTED, ACCEPTED, RUNNING, FINISHED, FAILED, KILLED
public enum JobState {
	
	// 未调度
	NONE,
	
	//等待派发
	NEW,
	
	//已派发
	ACCEPTED,
	
	//运行中
	RUNNING,
	
	//成功
	FINISHED,
	
	// 跳过
	SKIPPED,
	
	// 失败
	FAILED,
	
	// 取消
	CANCLED;
	
	
	public boolean isComplete() {
		return isSuccess() || isFailure();
	}

	// 任务已完成
	public boolean isSuccess() {
		return this == FINISHED || this == JobState.SKIPPED;
	}
	
	// 任务有错误
	public boolean isFailure() {
		return this == FAILED || this == JobState.CANCLED;
	}

	public String desc() {
		return null;
	}
}
