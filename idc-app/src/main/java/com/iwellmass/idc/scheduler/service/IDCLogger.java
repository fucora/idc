package com.iwellmass.idc.scheduler.service;

import com.iwellmass.idc.scheduler.model.ExecutionLog;

public interface IDCLogger {

	IDCLogger clearLog(String jobId);

	IDCLogger log(String jobId, String message, Object... args);

	IDCLogger log(ExecutionLog executionLog);

}
