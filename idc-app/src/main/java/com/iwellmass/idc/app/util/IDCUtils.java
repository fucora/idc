package com.iwellmass.idc.app.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.quartz.TriggerKey;

public class IDCUtils {
	
	public static final String GROUP_DEFAULT = "idc.default";
	
	
	
	public static final String REDO_GROUP = "idc";
	
	
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
	
	public static boolean isBlockingGroup(TriggerKey key) {
		return GROUP_DEFAULT.equals(key.getGroup());
	}
	
}
