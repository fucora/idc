package com.iwellmass.idc.jpa;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.persistence.AttributeConverter;

public class LocalDateTimeMillsConverter implements AttributeConverter<LocalDateTime, Long> {

	@Override
	public Long convertToDatabaseColumn(LocalDateTime attribute) {
		return attribute.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	@Override
	public LocalDateTime convertToEntityAttribute(Long dbData) {
		if (dbData == null || dbData == -1) {
			return null;
		}
		return Instant.ofEpochMilli(dbData).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
