package com.iwellmass.idc.app.vo;

import org.quartz.Trigger;

public class ReManualTaskVO extends ReTaskVO implements SimpleTriggerBuilder{

	@Override
	public Trigger buildTrigger(String name, String group) {
		return SimpleTriggerBuilder.super.buildTrigger(name, group);
	}
}
