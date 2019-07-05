package com.iwellmass.idc.scheduler.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.AttributeConverter;

public class LocalLongConverter implements AttributeConverter<LocalDateTime, Long> {

	@Override
	public Long convertToDatabaseColumn(LocalDateTime attribute) {
		if (attribute == null) {
			return null;
		}
		long mill = attribute.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return mill;
	}

	@Override
	public LocalDateTime convertToEntityAttribute(Long dbData) {
		if (dbData == null || dbData == -1) {
			return null;
		}
		
		return Instant.ofEpochMilli(dbData).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

}
