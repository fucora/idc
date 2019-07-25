package com.iwellmass.idc.scheduler.util;

import javax.persistence.AttributeConverter;

import com.iwellmass.idc.scheduler.model.TaskState;

public class TaskStateConverter implements AttributeConverter<TaskState, String>{

	@Override
	public String convertToDatabaseColumn(TaskState attribute) {
		throw new UnsupportedOperationException("Read-only attribute");
	}

	@Override
	public TaskState convertToEntityAttribute(String dbData) {
		if(dbData==null)
		{
			return  TaskState.NONE;
		}
		return TaskState.valueOf(dbData);
	}
	
}
