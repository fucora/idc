package com.iwellmass.idc.scheduler.util;

import java.util.Map;

public class MapConverter extends JsonConverter<Map<String, Object>> {

	public MapConverter() {
		super(mapper.getTypeFactory().constructRawMapType(Map.class));
	}
}
