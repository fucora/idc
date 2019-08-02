package com.iwellmass.idc.app.vo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import com.iwellmass.idc.app.util.IDCUtils;

public interface SimpleTriggerBuilder {

    default Trigger buildTrigger(TriggerKey key) {

        TriggerBuilder<SimpleTrigger> builder = TriggerBuilder.newTrigger()
                .withIdentity(key)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionIgnoreMisfires())
                .startNow();
        return builder.build();
    }

}
