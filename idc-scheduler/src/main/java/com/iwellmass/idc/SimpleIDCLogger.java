package com.iwellmass.idc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleIDCLogger implements IDCLogger{

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleIDCLogger.class);
	
	@Override
	public IDCLogger clearLog(Integer instanceId) {
		return this;
	}

	@Override
	public IDCLogger log(Integer instanceId, String message, Object... args) {
		LOGGER.info("[" + instanceId + "] >> " + message, args);
		return this;
	}

}
