package com.iwellmass.idc.jpa;

import com.iwellmass.core.convert.JsonStringConverter;
import com.iwellmass.idc.model.ScheduleProperties;

public class SchedulePropertiesConverter extends JsonStringConverter<ScheduleProperties> {
	@Override
	public ScheduleProperties convertToEntityAttribute(String dbData) {
		return convertToEntityAttribute(dbData, ScheduleProperties.class);
	}
}
