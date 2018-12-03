package com.iwellmass.idc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.quartz.Trigger;
import org.quartz.TriggerKey;

import com.alibaba.fastjson.JSON;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.quartz.IDCContextKey;

public class IDCUtils {
	
	
	public static <T> Function<IDCContextKey<String>, T> getObject(Map<String, Object> map, Class<T> type) {
		return ( key ) -> {
			String str =  key.applyGet(map);
			return JSON.parseObject(str, type);
		};
	}
	
	public static TriggerKey toTriggerKey(JobKey jobKey) {
		return new TriggerKey(jobKey.getJobId(), jobKey.getJobGroup());
	}
	
	public static TaskKey toTaskKey(Trigger trigger) {
		return new TaskKey(trigger.getJobKey().getName(), trigger.getJobKey().getGroup());
	}
	
	public static JobKey getSubJobKey(JobKey jobKey, TaskKey taskKey) {
		return new JobKey(taskKey.getTaskId(), taskKey.getTaskGroup());
	}
	
	public static final LocalDateTime toLocalDateTime(Long mill) {
		if (mill == null) {
			return null;
		}
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
	
	public static final LocalDateTime toLocalDateTime(Date date) {
		if (date == null) {
			return null;
		}
		long mill = date.getTime();
		return Instant.ofEpochMilli(mill).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

}
