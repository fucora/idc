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
	public CompletableFuture<CompleteEvent> async(IDCJob idcJob, IDCJobExecutionContext context) {

		CompleteEvent event = null;
		try {
			// 具体业务逻辑
			idcJob.execute(context);
			event = CompleteEvent.successEvent()
				.setMessage("执行成功")
				.setInstanceId(context.getInstance().getInstanceId());
		} catch (Throwable e) {
			event = CompleteEvent.failureEvent("执行失败", e);
		}
		
		return CompletableFuture.completedFuture(event);
	}
}
