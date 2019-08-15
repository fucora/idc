package com.iwellmass.idc.executor;

import com.iwellmass.idc.ExecuteRequest;
import org.slf4j.helpers.MessageFormatter;

public interface IDCJobExecutorService {

	void execute(ExecuteRequest executeRequest);

	static String toURI(String contentType) {
		return MessageFormatter.format("/idc-job/{}", contentType).getMessage();
	}
}
