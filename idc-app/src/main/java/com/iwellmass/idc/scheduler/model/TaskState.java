package com.iwellmass.idc.scheduler.model;

public enum TaskState {

	NORMAL;

	public boolean isRunning() {
		return true;
	}

	public String desc() {
		return name();
	}
}
