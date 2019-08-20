package com.iwellmass.idc.scheduler.service;

public interface IDCLogger {

	IDCLogger clearLog(String jobId);

	IDCLogger log(String jobId, String message, Object... args);

}
