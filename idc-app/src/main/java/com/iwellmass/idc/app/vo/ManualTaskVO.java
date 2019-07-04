package com.iwellmass.idc.app.vo;

import java.util.Map;

import org.quartz.Trigger;
import org.quartz.TriggerKey;

public class ManualTaskVO extends TaskVO implements SimpleTriggerBuilder{

	@Override
	public Map<String, Object> getProps() {
		return null;
	}

	@Override
	public Trigger buildTrigger(TriggerKey key) {
		return SimpleTriggerBuilder.super.buildTrigger(key);
	}
}
