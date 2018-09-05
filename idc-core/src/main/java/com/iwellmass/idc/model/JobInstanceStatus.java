package com.iwellmass.idc.model;

// copy-of-YARN :) NEW, NEW_SAVING, SUBMITTED, ACCEPTED, RUNNING, FINISHED, FAILED, KILLED
public enum JobInstanceStatus {
	
	//等待派发
	NEW,
	
	//已派发
	ACCEPTED,
	
	//运行中
	RUNNING,
	
	//成功
	FINISHED,
	
	// 失败
	FAILED
}