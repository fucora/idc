package com.iwellmass.idc.client.autoconfig;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCJob;
import com.iwellmass.idc.executor.IDCJobExecutionContext;

@Component
public class AsyncService {

	@Async("idc-executor")
	public CompletableFuture<CompleteEvent> async(IDCJobExecutionContext context) {

		CompleteEvent event = null;
		try {
			IDCJob idcJob = context.getIDCJob();
			// 具体业务逻辑
			idcJob.execute(context);
			event = CompleteEvent.successEvent()
				.setMessage("执行成功")
				.setInstanceId(context.getInstanceId());
		} catch (Throwable e) {
			event = CompleteEvent.failureEvent("执行失败", e);
		}
		
		return CompletableFuture.completedFuture(event);
	}
}
