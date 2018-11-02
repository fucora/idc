package com.iwellmass.idc.scheduler;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.quartz.IDCLogger;
import com.iwellmass.idc.repo.ExecutionLogRepository;

@Component
public class JpaIDCLogger implements IDCLogger {

	@Inject
	private ExecutionLogRepository logRepo;
	
	@Transactional
	@Override
	public IDCLogger clearLog(Integer instanceId) {
		logRepo.deleteByInstanceId(instanceId);
		return this;
	}

	@Transactional
	@Override
	public IDCLogger log(Integer instanceId, String message, Object... args) {
		logRepo.log(instanceId, message, args);
		return this;
	}

}
