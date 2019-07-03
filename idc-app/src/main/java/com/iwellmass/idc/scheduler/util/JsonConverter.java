package com.iwellmass.idc.scheduler.util;

import java.io.IOException;

import javax.persistence.AttributeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonConverter<T> implements AttributeConverter<T, String> {

	static final Logger LOGGER = LoggerFactory.getLogger(JsonConverter.class);
	static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private final JavaType type;

	public JsonConverter(JavaType type) {
		this.type = mapper.constructType(type);
	}

	@Override
	public String convertToDatabaseColumn(T attribute) {
		if (attribute == null) {
			return null;
		}
		try {
			return mapper.writeValueAsString(attribute);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public T convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return null;
		}
		try {
			return type == null ? mapper.readerFor(type).readValue(dbData)
					: mapper.readerFor(type).readValue(dbData);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

}
