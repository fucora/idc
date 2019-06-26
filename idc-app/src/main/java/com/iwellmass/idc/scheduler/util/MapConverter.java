package com.iwellmass.idc.scheduler.util;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

public class MapConverter extends JsonConverter<Map<String, Object>> {

	public MapConverter() {
		super(new TypeReference<Map<String, Object>>() {
		});
	}
}
