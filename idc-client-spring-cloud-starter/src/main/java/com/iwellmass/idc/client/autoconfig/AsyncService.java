package com.iwellmass.idc.client.autoconfig;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCJob;
import com.iwellmass.idc.executor.IDCJobExecutionContext;

@Component
public class AsyncService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncService.class);
	

	@Async("idc-executor")
	public CompletableFuture<CompleteEvent> async(IDCJob idcJob, IDCJobExecutionContext context) {

		int instanceId = context.getInstance().getInstanceId();
		
		CompleteEvent event = null;
		try {
			// 具体业务逻辑
			idcJob.execute(context);
			event = CompleteEvent.successEvent("执行成功");
		} catch (Throwable e) {
			LOGGER.info(e.getMessage(), e);
			event = CompleteEvent.failureEvent("执行失败", e);
		}
		
		
		return CompletableFuture.completedFuture(event.setInstanceId(instanceId));
	}
}
