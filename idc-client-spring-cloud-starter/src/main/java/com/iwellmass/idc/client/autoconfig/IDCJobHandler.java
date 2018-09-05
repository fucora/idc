package com.iwellmass.idc.client.autoconfig;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCJob;
import com.iwellmass.idc.executor.IDCJobExecutionContext;
import com.iwellmass.idc.executor.IDCJobExecutorService;
import com.iwellmass.idc.model.JobInstance;

/**
 * 调度器 Rest 接口
 */
public class IDCJobHandler implements IDCJobExecutorService{

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCJobHandler.class);

	private final IDCJob job;
	private IDCExecutionContextFactory contextFactory;
	private IDCStatusManagerClient idcStatusManagerClient;
	private AsyncService asyncService;
	
	public IDCJobHandler(IDCJob job) {
		this.job = job;
	}

	@ResponseBody
	@PostMapping
	public void execute(@RequestBody JobInstance jobInstance) {
		IDCJobExecutionContext context = contextFactory.newContext(jobInstance, job);
		// safe execute
		execute(context);
		LOGGER.info("IDCJob[id={}, groupId={}, taskId={}] accepted, timestamp: {}", jobInstance.getInstanceId(),
				jobInstance.getTaskId(), jobInstance.getGroupId(), System.currentTimeMillis());
	}
	
	public void execute(IDCJobExecutionContext context) {
		LOGGER.info("执行任务 {}", context.getInstanceId());
		CompletableFuture<CompleteEvent> futrue = asyncService.async(context);
		/*if (context.isAsync()) {
			futrue.get();
		}*/
		futrue.whenCompleteAsync(this::notifyStatusServer);
	}
	
	private void notifyStatusServer(CompleteEvent event, Throwable e) {
		LOGGER.info("任务 {} 执行完毕, 执行结果: {}", event.getInstanceId(), event.getFinalStatus());
		if (e != null) {
			event = CompleteEvent.failureEvent("execute failured, report this bug...", e);
		}
		this.idcStatusManagerClient.fireCompleteEvent(event);
	}
	

	public void setContextFactory(IDCExecutionContextFactory contextFactory) {
		this.contextFactory = contextFactory;
	}

	public void setIdcStatusManagerClient(IDCStatusManagerClient idcStatusManagerClient) {
		this.idcStatusManagerClient = idcStatusManagerClient;
	}
	
	public void setAsyncService(AsyncService asyncService) {
		this.asyncService = asyncService;
	}

	
}