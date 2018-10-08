package com.iwellmass.idc.model;

import javax.persistence.AttributeConverter;

public class ScheduleStatusConverter implements AttributeConverter<ScheduleStatus, String> {

	@Override
	public String convertToDatabaseColumn(ScheduleStatus attribute) {
		return attribute.toString();
	}

	@Override
	public ScheduleStatus convertToEntityAttribute(String ts) {
		 if (ts == null) {
             return ScheduleStatus.NONE;
         }

         if (ts.equals("STATE_DELETED")) {
             return ScheduleStatus.NONE;
         }

         if (ts.equals("STATE_COMPLETE")) {
             return ScheduleStatus.COMPLETE;
         }

         if (ts.equals("STATE_PAUSED")) {
             return ScheduleStatus.PAUSED;
         }

         if (ts.equals("STATE_PAUSED_BLOCKED")) {
             return ScheduleStatus.PAUSED;
         }

         if (ts.equals("STATE_ERROR")) {
             return ScheduleStatus.ERROR;
         }

         if (ts.equals("STATE_BLOCKED")) {
             return ScheduleStatus.BLOCKED;
         }
         
         return ScheduleStatus.NORMAL;
	}
}
