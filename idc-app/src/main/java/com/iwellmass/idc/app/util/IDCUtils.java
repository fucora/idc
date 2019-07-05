package com.iwellmass.idc.app.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.lang.Nullable;

public class IDCUtils {
	
	public static final LocalDateTime toLocalDateTime(Long mill) {
		if (mill == null) {
			return null;
		}
		return Instant.ofEpochMilli(mill).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * {@link LocalDateTime} --&gt; {@link Date}
	 * @param localDateTime
	 * @return date
	 */
	public static final Date toDate(@Nullable LocalDateTime localDateTime) {
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
