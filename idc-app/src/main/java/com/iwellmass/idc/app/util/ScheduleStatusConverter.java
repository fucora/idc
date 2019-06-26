package com.iwellmass.idc.app.util;

import javax.persistence.AttributeConverter;

import com.iwellmass.idc.scheduler.model.JobState;

public class ScheduleStatusConverter implements AttributeConverter<JobState, String> {

	@Override
	public String convertToDatabaseColumn(JobState attribute) {
		return attribute.toString();
	}

	@Override
	public JobState convertToEntityAttribute(String ts) {
		 if (ts == null) {
             return JobState.NONE;
         }

         if (ts.equals("STATE_DELETED")) {
             return JobState.NONE;
         }

//         if (ts.equals("STATE_COMPLETE")) {
//             return TaskState.COMPLETE;
//         }
//
//         if (ts.equals("STATE_PAUSED")) {
//             return TaskState.PAUSED;
//         }
//
//         if (ts.equals("STATE_PAUSED_BLOCKED")) {
//             return TaskState.PAUSED;
//         }
//
//         if (ts.equals("STATE_ERROR")) {
//             return TaskState.ERROR;
//         }
//
//         if (ts.equals("STATE_BLOCKED")) {
//             return TaskState.BLOCKED;
//         }
         
         return JobState.NONE;
	}
}
