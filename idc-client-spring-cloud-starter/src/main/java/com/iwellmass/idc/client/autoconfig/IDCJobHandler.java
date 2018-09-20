package com.iwellmass.idc.client.autoconfig;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

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
	public void execute(@RequestBody JobInstance jobInstance) {
		// safe execute
		executeAsync(jobInstance);
		LOGGER.info("IDCJob[id={}, groupId={}, taskId={}] accepted, timestamp: {}", jobInstance.getInstanceId(),
				jobInstance.getTaskId(), jobInstance.getGroupId(), System.currentTimeMillis());
	}

	public void executeAsync(JobInstance instance) {
		LOGGER.info("执行任务 {}", instance);
		CompletableFuture<CompleteEvent> futrue = CompletableFuture.supplyAsync(() -> safeExecute(instance), executor);
		futrue.whenCompleteAsync(this::fireCompleteEvent);
	}

	private CompleteEvent safeExecute(JobInstance instance) {
		ExecutionContextImpl context = newContext(instance);
		context.instance = instance;
		try {
			this.job.execute(context);
		} catch (Throwable e) {
			context.event = CompleteEvent.failureEvent("执行失败: " + e.getMessage()); // unexpect exception
		}

		CompleteEvent event = context.event;
		if (event == null) {
			event = CompleteEvent.successEvent();
		}
		event.setInstanceId(context.getInstance().getInstanceId()).setEndTime(LocalDateTime.now());
		return event;
	}

	private ExecutionContextImpl newContext(JobInstance instance) {
		ExecutionContextImpl context = new ExecutionContextImpl();
		return context;
	}

	private final void fireCompleteEvent(CompleteEvent event, Throwable cause) {
		LOGGER.info("任务 {} 执行完毕, 执行结果: {}", event.getInstanceId(), event.getFinalStatus());
		if (cause != null) {
			event = CompleteEvent.failureEvent("execute failured, report this bug...", cause);
		}
		try {
			idcStatusService.fireCompleteEvent(event);
		} catch (Throwable e) {
			LOGGER.info("无法通知状态服务器: ", e.getMessage(), e);
		}
	}

	class ExecutionContextImpl implements IDCJobExecutionContext {

		private JobInstance instance;
		private CompleteEvent event;

		@Override
		public JobInstance getInstance() {
			return instance;
		}

		@Override
		public void complete(CompleteEvent event) {
			this.event = event;
		}
	}
}