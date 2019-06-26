package com.iwellmass.idc.scheduler.util;

import java.io.IOException;

import javax.persistence.AttributeConverter;
import javax.persistence.PersistenceException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonConverter<T> implements AttributeConverter<T, String> {

	static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private final Class<T> type;

	private final TypeReference<T> typeRef;

	public JsonConverter(Class<T> type) {
		this.type = type;
		this.typeRef = null;
	}

	public JsonConverter(TypeReference<T> typeRef) {
		this.typeRef = typeRef;
		this.type = null;
	}

	@Override
	public String convertToDatabaseColumn(T attribute) {
		if (attribute == null) {
			return null;
		}
		try {
			return mapper.writeValueAsString(attribute);
		} catch (IOException e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

	@Override
	public T convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty()) {
			return null;
		}
		try {
			return type == null ? mapper.readerFor(typeRef).readValue(dbData)
					: mapper.readerFor(type).readValue(dbData);
		} catch (IOException e) {
			throw new PersistenceException(e.getMessage(), e);
		}
	}

}
