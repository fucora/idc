package com.iwellmass.idc.app.vo.task;

import java.time.LocalTime;
import java.util.List;

import org.quartz.Trigger;
import org.quartz.TriggerKey;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.model.CronType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReCronTaskVO extends ReTaskVO implements CronTriggerBuilder{

	@ApiModelProperty("周期类型")
	private CronType cronType;

	@ApiModelProperty("哪一天")
	private List<Integer> days;

	@ApiModelProperty("具体时间")
	@JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
	private LocalTime duetime = LocalTime.MIN;

	@Override
	public Trigger buildTrigger(TriggerKey key) {
		return CronTriggerBuilder.super.buildTrigger(key);
	}
}
