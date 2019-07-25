package com.iwellmass.idc.app.vo.task;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Trigger;
import org.quartz.TriggerKey;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.model.CronType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronTaskVO extends TaskVO implements CronTriggerBuilder {

	@ApiModelProperty("周期类型")
	private CronType cronType;

	@ApiModelProperty("哪一天")
	private List<Integer> days;

	@Override
	public Map<String, Object> getProps() {
		Map<String, Object> props = new HashMap<>();
		props.put("cronType", cronType);
		props.put("days", days);
		return props;
	}

	@Override
	public Trigger buildTrigger(TriggerKey key) {
		return CronTriggerBuilder.super.buildTrigger(key);
	}
}
