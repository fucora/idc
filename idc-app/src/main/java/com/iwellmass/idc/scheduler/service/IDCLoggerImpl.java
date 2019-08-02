package com.iwellmass.idc.scheduler.service;

import com.iwellmass.idc.scheduler.repository.ExecutionLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Component
public class IDCLoggerImpl implements IDCLogger {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCLoggerImpl.class);
	
	@Inject
	private ExecutionLogRepository logRepo;
	
	@Transactional
	@Override
	public IDCLogger clearLog(String instanceId) {
		try {
			logRepo.deleteByInstanceId(instanceId);
		} catch (Throwable e) {
			LOGGER.error("clear failured.");
		}
		return this;
	}

	@Override
	public IDCLogger log(String instanceId, String message, Object... args) {
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
