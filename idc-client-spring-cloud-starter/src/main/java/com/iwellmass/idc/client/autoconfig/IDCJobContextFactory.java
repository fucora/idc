package com.iwellmass.idc.client.autoconfig;

import com.iwellmass.idc.ExecuteRequest;
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

		@Override
		public void fail(Throwable t) {

		}

		private ExecuteRequest executeRequest;
		private int state = RUNNING; // TODO use CAS

		@Override
		public ExecuteRequest getExecuteRequest() {
			return executeRequest;
		}

		@Override
		public void complete(CompleteEvent event) {
			Objects.requireNonNull(event, "event 不能为空");

			if (state == COMPLETE) {
				LOGGER.warn("[{}] job already complete {}", event.getNodeJobId());
				return;
			}

			LOGGER.info("[{}] 任务 '{}' 执行完毕, 执行结果: {}", event.getNodeJobId(),
					executeRequest.getTaskName(),
					event.getFinalStatus());

			try {
				idcStatusService.complete(event);
				state = COMPLETE;
			} catch (Throwable e) {
				state = NOTIFY_ERROR;
				LOGGER.error("发送事件失败, EVENT: {}", event, e);
			}
		}



		public CompleteEvent newCompleteEvent(JobInstanceStatus status,String nodeTaskName) {
			if (status == JobInstanceStatus.FINISHED) {
				return CompleteEvent.successEvent(executeRequest.getNodeJobId(),nodeTaskName);
			} else if (status == JobInstanceStatus.FAILED) {
				return CompleteEvent.failureEvent(executeRequest.getNodeJobId(),nodeTaskName);
			} else {
				return CompleteEvent.failureEvent(executeRequest.getNodeJobId(),nodeTaskName).setFinalStatus(status);
			}
		}

		public ProgressEvent newProgressEvent() {
			return ProgressEvent.newEvent(executeRequest.getNodeJobId());
		}
		public StartEvent newStartEvent() {
			return StartEvent.newEvent(executeRequest.getNodeJobId());
		}	}


}
