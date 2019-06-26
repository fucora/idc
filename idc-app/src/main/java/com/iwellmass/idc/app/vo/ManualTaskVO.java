package com.iwellmass.idc.app.vo;

import java.util.Map;

import org.quartz.Trigger;

public class ManualTaskVO extends TaskVO implements SimpleTriggerBuilder{

	@Override
	public Map<String, Object> getProps() {
		return null;
	}

	@Override
	public Trigger buildTrigger(String name, String group) {
		return SimpleTriggerBuilder.super.buildTrigger(name, group);
	}
}
