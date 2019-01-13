package com.iwellmass.idc.app.scheduler;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.IDCLogger;
import com.iwellmass.idc.app.repo.ExecutionLogRepository;

public class IDCLoggerImpl implements IDCLogger {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCLoggerImpl.class);
	
	@Inject
	private ExecutionLogRepository logRepo;
	
	@Transactional
	@Override
	public IDCLogger clearLog(Integer instanceId) {
		try {
			logRepo.deleteByInstanceId(instanceId);
		} catch (Throwable e) {
			LOGGER.error("clear failured.");
		}
		return this;
	}

	@Transactional
	@Override
	public IDCLogger log(Integer instanceId, String message, Object... args) {
		try {
			if (message.contains("执行失败")) {
				new RuntimeException().printStackTrace();
			}
			logRepo.log(instanceId, message, args);
		} catch (Throwable e) {
			LOGGER.info("INS[" + instanceId + "] >> " + message, args);
		}
		return this;
	}

}
