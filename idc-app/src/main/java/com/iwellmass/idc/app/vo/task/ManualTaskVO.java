package com.iwellmass.idc.app.vo.task;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

@Getter
@Setter
public class ManualTaskVO extends TaskVO implements SimpleTriggerBuilder {

    @Override
    public Map<String, Object> getProps() {
        return null;
    }

    @Override
    public Trigger buildTrigger(TriggerKey key) {
        return SimpleTriggerBuilder.super.buildTrigger(key);
    }
}
