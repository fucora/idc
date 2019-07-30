package com.iwellmass.idc.client.autoconfig;

import com.iwellmass.idc.executor.*;
import com.iwellmass.idc.model.JobInstanceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.JobEnv;

import java.util.Objects;

import static java.awt.MediaTracker.COMPLETE;

@Component
public class IDCJobContextFactory {

	static final Logger LOGGER = LoggerFactory.getLogger(IDCJobContextFactory.class);
//	@Autowired
//	IDCStatusService idcStatusService;

	@Autowired
	IDCJobContext idcStatusService;
	@Bean
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public IDCJobContext newContext(JobEnv jobEnv) {
		return null;
	}

	private static final int RUNNING = 0x01;
	private static final int COMPLETE = 0x02;
	private static final int NOTIFY_ERROR = 0x03;

	private class ExecutionContextImpl implements IDCJobContext {

		private JobEnv jobEnv;
		private int state = RUNNING; // TODO use CAS

		@Override
		public JobEnv getJobEnv() {
			return jobEnv;
		}

		@Override
		public void complete(CompleteEvent event) {
			Objects.requireNonNull(event, "event 不能为空");

			if (state == COMPLETE) {
				LOGGER.warn("[{}] job already complete {}", event.getInstanceId());
				return;
			}

			LOGGER.info("[{}] 任务 '{}' 执行完毕, 执行结果: {}", event.getInstanceId(),
					jobEnv.getJobName(),
					event.getFinalStatus());

			try {
				idcStatusService.complete(event);
				state = COMPLETE;
			} catch (Throwable e) {
				state = NOTIFY_ERROR;
				LOGGER.error("发送事件失败, EVENT: {}", event, e);
			}
		}


		public CompleteEvent newCompleteEvent(JobInstanceStatus status) {
			if (status == JobInstanceStatus.FINISHED) {
				return CompleteEvent.successEvent(jobEnv.getInstanceId());
			} else if (status == JobInstanceStatus.FAILED) {
				return CompleteEvent.failureEvent(jobEnv.getInstanceId());
			} else {
				return CompleteEvent.failureEvent(jobEnv.getInstanceId()).setFinalStatus(status);
			}
		}

		public ProgressEvent newProgressEvent() {
			return ProgressEvent.newEvent(jobEnv.getInstanceId());
		}
		public StartEvent newStartEvent() {
			return StartEvent.newEvent(jobEnv.getInstanceId());
		}
	}

}
