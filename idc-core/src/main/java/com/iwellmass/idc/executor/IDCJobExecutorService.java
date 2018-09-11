package com.iwellmass.idc.executor;

import org.slf4j.helpers.MessageFormatter;

import com.iwellmass.idc.model.JobInstance;

public interface IDCJobExecutorService {

	public void execute(JobInstance context);

	public static String toURI(String contentType) {
		return MessageFormatter.format("/idc-job/{}", contentType).getMessage();
	}
}
