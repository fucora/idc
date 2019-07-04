package com.iwellmass.idc.app.vo;

import org.quartz.Trigger;
import org.quartz.TriggerKey;

public class ReManualTaskVO extends ReTaskVO implements SimpleTriggerBuilder{

	@Override
	public Trigger buildTrigger(TriggerKey key) {
		return SimpleTriggerBuilder.super.buildTrigger(key);
	}
}
