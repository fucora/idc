package com.iwellmass.idc.autoconfig;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCJob;
import com.iwellmass.idc.model.JobInstance;

public class IDCJobEndpoint {

	@Inject
	private DefaultIDCExecutorService executorService;
	
	private IDCJob job;

	public IDCJobEndpoint(IDCJob job) {
		this.job = job;
	}

	@ResponseBody
	@PostMapping("/execution")
	public ServiceResult<String> execution(@RequestBody JobInstance jobInstance) {

		CompletableFuture<CompleteEvent> future = executorService.execute(job);
		
		future.whenComplete((evt, exception) -> {
			if (exception != null) {
				// TODO 
			}
		});
		
		return ServiceResult.success(String.format("IDCJob[id=%s, groupId=%s, taskId=%s] accepted, timestamp: %s", 
				jobInstance.getInstanceId(), jobInstance.getTaskId(), jobInstance.getGroupId(),  System.currentTimeMillis()));
	}

}
