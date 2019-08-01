package com.iwellmass.idc.scheduler.service;

public interface IDCLogger {

	IDCLogger clearLog(String instanceId);

	IDCLogger log(String instanceId, String message, Object... args);

}
