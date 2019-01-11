package com.iwellmass.idc.quartz;

public enum IDCTriggerInstruction {
	
	MAIN,
	
	SUB,
	
	REDO,
	
	GUARD;

	/**
	 * 是否 IDCJob 被触发
	 */
	public boolean isIDCJobTriggered() {
		return this != GUARD;
	}
}
