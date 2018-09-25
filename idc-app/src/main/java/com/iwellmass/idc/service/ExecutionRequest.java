package com.iwellmass.idc.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.ScheduleType;

public class ExecutionRequest extends JobPK {

	private static final long serialVersionUID = -8693972886473180967L;

	private String loadDate;
	
	private String jobParameter;
	
	public String getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(String loadDate) {
		this.loadDate = loadDate;
	}
	
	public String getJobParameter() {
		return jobParameter;
	}

	public void setJobParameter(String jobParameter) {
		this.jobParameter = jobParameter;
	}
	
	public final LocalDateTime getAsLocalDateTime(ScheduleType type) {
		switch (type) {
		case DAILY:
			return LocalDateTime.of(LocalDate.parse(loadDate, DateTimeFormatter.BASIC_ISO_DATE), LocalTime.MIN);
		case MONTHLY: 
			return LocalDateTime.of(LocalDate.parse(loadDate + "01", DateTimeFormatter.BASIC_ISO_DATE), LocalTime.MIN);
		default:
			throw new UnsupportedOperationException("unsupported scheduleType" + type);
		}
	}

}
