package com.iwellmass.idc.quartz;

public interface IDCLogger {

	IDCLogger clearLog(Integer instanceId);

	IDCLogger log(Integer instanceId, String message, Object...args);

}
