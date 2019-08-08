package com.iwellmass.idc.app.vo.task;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iwellmass.common.util.Utils;
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

    @ApiModelProperty("cron表达式(仅自定义周期时需要)")
    private String expression;

    @ApiModelProperty("具体时间")
    @JsonFormat(timezone = "GMT+8", pattern = "HH:mm:ss")
    LocalTime duetime = LocalTime.MIN;

    @Override
    public Map<String, Object> getProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("cronType", cronType);
        props.put("days", days);
        props.put("expression", Utils.isNullOrEmpty(expression) ? "" : expression);
        return props;
    }

    @Override
    public Trigger buildTrigger(TriggerKey key) {
        return CronTriggerBuilder.super.buildTrigger(key);
    }
}
