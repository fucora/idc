package com.iwellmass.idc.app.vo.task;

import org.quartz.Trigger;
import org.quartz.TriggerKey;

import java.util.Map;

public class ReManualTaskVO extends ReTaskVO implements SimpleTriggerBuilder{

	@Override
	public Map<String, Object> getProps() {
		return null;
	}

	@Override
	public Trigger buildTrigger(TriggerKey key) {
		return SimpleTriggerBuilder.super.buildTrigger(key);
	}
}
