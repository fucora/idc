package com.iwellmass.idc.app.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.iwellmass.idc.app.util.IDCUtils;

public interface SimpleTriggerBuilder {

	LocalDate getStartDate();

	LocalDate getEndDate();

	default Trigger buildTrigger(String name, String group) {

		TriggerBuilder<SimpleTrigger> builder = TriggerBuilder.newTrigger().withIdentity(name, group)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule());

		if (getStartDate() != null) {
			builder.startAt(IDCUtils.toDate(LocalDateTime.of(getStartDate(), LocalTime.MIN)));
		}

		if (getEndDate() != null) {
			builder.endAt(IDCUtils.toDate(LocalDateTime.of(getEndDate(), LocalTime.MAX)));
		}
		return builder.build();
	}

}
