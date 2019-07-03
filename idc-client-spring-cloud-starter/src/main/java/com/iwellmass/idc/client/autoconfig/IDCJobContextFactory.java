package com.iwellmass.idc.client.autoconfig;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.JobEnv;
import com.iwellmass.idc.executor.IDCJobContext;

@Component
public class IDCJobContextFactory {

	@Bean
	@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public IDCJobContext newContext(JobEnv jobEnv) {
		return null;
	}
	
	
	private class ExecutionContextImpl implements IDCJobContext {

//		private JobEnv jobEnv;
//		private int state = RUNNING; // TODO use CAS
//		
//		@Override
//		public JobEnv getJobEnv() {
//			return jobEnv;
//		}
//
//		@Override
//		public void complete(CompleteEvent event) {
//			Objects.requireNonNull(event, "event 不能为空");
//
//			if (state == COMPLETE) {
//				LOGGER.warn("[{}] job already complete {}", event.getInstanceId());
//				return;
//			}
//			
//			LOGGER.info("[{}] 任务 '{}' 执行完毕, 执行结果: {}", event.getInstanceId(), 
//					jobEnv.getJobName(),
//					event.getFinalStatus());
//			
//			try {
//				idcStatusService.complete(event);
//				state = COMPLETE;
//			} catch (Throwable e) {
//				state = NOTIFY_ERROR;
//				LOGGER.error("发送事件失败, EVENT: {}", event, e);
//			}
//		}
//		
//		
//		public CompleteEvent newCompleteEvent(JobInstanceStatus status) {
//			if (status == JobInstanceStatus.FINISHED) {
//				return CompleteEvent.successEvent(jobEnv.getInstanceId());
//			} else if (status == JobInstanceStatus.FAILED) {
//				return CompleteEvent.failureEvent(jobEnv.getInstanceId());
//			} else {
//				return CompleteEvent.failureEvent(jobEnv.getInstanceId()).setFinalStatus(status);
//			}
//		}
//		
//		public ProgressEvent newProgressEvent() {
//			return ProgressEvent.newEvent(jobEnv.getInstanceId());
//		}
//		public StartEvent newStartEvent() {
//			return StartEvent.newEvent(jobEnv.getInstanceId());
//		}
	}

}
