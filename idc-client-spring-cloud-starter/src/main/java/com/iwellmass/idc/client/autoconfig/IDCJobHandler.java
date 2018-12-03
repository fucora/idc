package com.iwellmass.idc.client.autoconfig;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCJob;
import com.iwellmass.idc.executor.IDCJobExecutionContext;
import com.iwellmass.idc.executor.IDCJobExecutorService;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.model.JobInstance;

/**
 * 调度器 Rest 接口
 */
public class IDCJobHandler implements IDCJobExecutorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCJobHandler.class);
	
	private static final int RUNNING = 0x01;
	private static final int COMPLETE = 0x02;
	private static final int NOTIFY_ERROR = 0x03;

	private final IDCJob job;

	@Inject
	private IDCStatusService idcStatusService;

	@Inject
	@Named("idc-executor")
	private AsyncTaskExecutor executor;

	public IDCJobHandler(IDCJob job) {
		this.job = job;
	}

	@ResponseBody
	@PostMapping(path = "/execution")
	public ServiceResult<String> doExecute(@RequestBody JobInstance jobInstance) {
		// safe execute
		execute(jobInstance);
		LOGGER.info("任务 {} [groupId={}, taskId={}] accepted, timestamp: {}", jobInstance.getInstanceId(),
				jobInstance.getTaskId(), jobInstance.getTaskGroup(), System.currentTimeMillis());
		return ServiceResult.success("任务已提交");
	}
	
	public void execute(JobInstance instance) {
		
		ExecutionContextImpl context = new ExecutionContextImpl();
		context.instance = instance;
		
		CompletableFuture.runAsync(() -> job.execute(context), executor)
		.whenComplete((_void, cause) -> {
			if (cause != null) {
				CompleteEvent event = CompleteEvent.failureEvent("任务 {} 执行异常: {}", instance.getInstanceId(), cause.getMessage())
						.setInstanceId(context.getInstance().getInstanceId())
						.setEndTime(LocalDateTime.now());
				context.complete(event);
			}
			if (!context.isComplete()) {
				LOGGER.warn("任务 {} 已结束但未通知调度中心, 请确认异步通知可用", instance.getInstanceId());
			}
		});
	}

	private class ExecutionContextImpl implements IDCJobExecutionContext {

		private JobInstance instance;
		private int state = RUNNING; // TODO use CAS
		
		@Override
		public JobInstance getInstance() {
			return instance;
		}

		@Override
		public void complete(CompleteEvent event) {
			Objects.requireNonNull(event, "event 不能为空");

			if (state == COMPLETE) {
				LOGGER.warn("job {} already complete {}", event.getInstanceId());
				return;
			}
			
			event.setInstanceId(instance.getInstanceId());
			
			LOGGER.info("任务 {} 执行完毕, 执行结果: {}", event.getInstanceId(), event.getFinalStatus());
			
			try {
				idcStatusService.fireCompleteEvent(event);
				state = COMPLETE;
			} catch (Throwable e) {
				state = NOTIFY_ERROR;
				LOGGER.error("发送事件失败, EVENT: {}", event, e);
			}
		}
		
		public boolean isComplete() {
			return state == COMPLETE || state == NOTIFY_ERROR;
		}
	}
}