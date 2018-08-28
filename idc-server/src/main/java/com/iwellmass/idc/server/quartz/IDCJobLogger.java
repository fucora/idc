package com.iwellmass.idc.server.quartz;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.iwellmass.idc.model.JobExecutionLog;
import com.iwellmass.idc.repo.JobExecuteLogRepository;

public class IDCJobLogger {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCJobLogger.class);

	private String instanceId;
	private JobExecuteLogRepository executeLogRepo;

	public IDCJobLogger(String instanceId, JobExecuteLogRepository executeLogRepository) {
		this.instanceId = instanceId;
		this.executeLogRepo = executeLogRepository;
	}

	public void info(String message, Object... args) {
		String finalMessge = args == null || args.length == 0 ? message : format(message, args);
		try {
			JobExecutionLog log = new JobExecutionLog();
			log.setMessage(finalMessge);
			log.setInstanceId(this.instanceId);
			log.setTime(LocalDateTime.now());
			executeLogRepo.save(log);
		} catch (Exception e) {
			// ignore
			LOGGER.warn("记录执行消息[{}]时失败: {}", message, e.getMessage());
		}
	}

	private static final String format(String message, Object... args) {
		return MessageFormatter.arrayFormat(message, args).getMessage();
	}
}
