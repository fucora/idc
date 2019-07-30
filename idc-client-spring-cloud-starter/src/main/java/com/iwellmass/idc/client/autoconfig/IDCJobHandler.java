package com.iwellmass.idc.client.autoconfig;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.JobEnv;
import com.iwellmass.idc.executor.*;
import com.iwellmass.idc.model.JobInstanceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
	public ServiceResult<String> doExecute(@RequestBody JobEnvImpl jobEnv) {
		LOGGER.info("[{}] job '{}' accepted, taskId: {}", jobEnv.getInstanceId(),
				jobEnv.getJobName(), jobEnv.getTaskId());
		
		
		Map<String, String> ps = null;
		List<ExecParam> eps = jobEnv.getParameter();
		if (eps != null && !eps.isEmpty()) {
			ps = new HashMap<>();
			for (ExecParam ep : eps) {
				ps .put(ep.getName(), ep.getValue());
			}
		}
		
		LOGGER.info("[{}] parameter: {}", jobEnv.getInstanceId(), jobEnv.getJobName(), ps);
		// safe execute
		execute(jobEnv);
		return ServiceResult.success("任务已提交");
	}
	
	public void execute(JobEnv instance) {
		
		ExecutionContextImpl context = new ExecutionContextImpl();
		context.jobEnv = instance;
		
		CompletableFuture.runAsync(() -> job.execute(context), executor)
		.whenComplete((_void, cause) -> {
			if (cause != null) {
				CompleteEvent event = CompleteEvent.failureEvent(context.jobEnv.getInstanceId())
					.setMessage("任务 {} 执行异常: {}", cause.getMessage())
					.setEndTime(LocalDateTime.now());
				context.complete(event);
			}
		});
	}

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
				idcStatusService.fireCompleteEvent(event);
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