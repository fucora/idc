package com.iwellmass.idc.model;

import com.iwellmass.core.convert.JsonStringConverter;

public class SchedulePropertiesConverter extends JsonStringConverter<ScheduleProperties>{
	@Override
	public ScheduleProperties convertToEntityAttribute(String dbData) {
		return convertToEntityAttribute(dbData, ScheduleProperties.class);
	}
}
