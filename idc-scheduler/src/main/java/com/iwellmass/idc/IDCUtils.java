package com.iwellmass.idc;

import static com.iwellmass.idc.quartz.IDCContextKey.JOB_GROUP;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_ID;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_REOD;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.quartz.IDCContextKey;

public class IDCUtils {

	public static JobKey parseJobKey(Trigger trigger) {
		return parseJobInstanceKey(trigger)._1;
	}
	
	public static TwoTuple<JobKey, Long> parseJobInstanceKey(Trigger trigger) {
		JobDataMap jdm = trigger.getJobDataMap();
		boolean isRedo = JOB_REOD.applyGet(jdm);
		JobKey key = null;
		if (isRedo) {
			key = new JobKey(JOB_ID.applyGet(jdm), JOB_GROUP.applyGet(jdm));
		} else {
			key = new JobKey(trigger.getKey().getName(), trigger.getKey().getGroup());
		}
		
		return new TwoTuple<JobKey, Long>(key, 
			Optional.ofNullable(trigger.getPreviousFireTime()).map(Date::getTime).orElse(-1L));
	}
	
	public static LocalDateTime parseLoadDate(Trigger trigger, JobExecutionContext context) {
		JobDataMap jdm = trigger.getJobDataMap();
		boolean isRedo = JOB_REOD.applyGet(jdm);
		if (isRedo) {
			return IDCContextKey.CONTEXT_LOAD_DATE.applyGet(trigger.getJobDataMap());
		} else {
			return toLocalDateTime(context.getScheduledFireTime());
		}
	}
	
	public static JobKey asJobKey(TriggerKey triggerKey) {
		return new JobKey(triggerKey.getName(), triggerKey.getGroup());
	}
	
	public static TriggerKey asTriggerKey(JobKey jobKey) {
		return new TriggerKey(jobKey.getJobId(), jobKey.getJobGroup());
	}
	
	public static final LocalDateTime toLocalDateTime(Date date) {
		if (date == null) {
			return null;
		}
		long mill = date.getTime();
		return Instant.ofEpochMilli(mill).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static final Date toDate(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		long mill = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return new Date(mill);
	}

	public static Long toEpochMilli(LocalDateTime loadDate) {
		if (loadDate == null) {
			return -1L;
		}
		return loadDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static final <T> List<T> nullable(List<T> list) {
		return list == null ? Collections.emptyList() : list;
	}

	public static TaskKey getTaskKey(Trigger trigger) {
		return new TaskKey(trigger.getJobKey().getName(), trigger.getJobKey().getGroup());
	}
}
