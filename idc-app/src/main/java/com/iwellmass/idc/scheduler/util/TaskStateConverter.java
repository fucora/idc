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
			// 当没有对应的trigger信息时只能是整个trigger执行完成,trigger信息被quartz删除，其他状态,trigger信息将保存
			return  TaskState.COMPLETE;
		}
		return TaskState.valueOf(dbData);
	}
	
}
