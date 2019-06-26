package com.iwellmass.idc.executor;

import org.slf4j.helpers.MessageFormatter;

import com.iwellmass.idc.model.JobEnv;

public interface IDCJobExecutorService {

	void execute(JobEnv context);

	static String toURI(String contentType) {
		return MessageFormatter.format("/idc-job/{}", contentType).getMessage();
	}
}
