package com.iwellmass.idc.app.vo.task;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

@Getter
public class ManualTaskVO extends TaskVO implements SimpleTriggerBuilder{

	@ApiModelProperty("具体时间")
	@JsonFormat(timezone = "GMT+8", pattern = "HHmmss")
	private LocalTime duetime = LocalTime.MIN;

	@Override
	public Map<String, Object> getProps() {
		Map<String, Object> props = new HashMap<>();
		props.put("duetime", duetime);
		return props;
	}

	@Override
	public Trigger buildTrigger(TriggerKey key) {
		return SimpleTriggerBuilder.super.buildTrigger(key);
	}
}
