package com.iwellmass.idc.client.autoconfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.JobEnv;
import com.iwellmass.idc.executor.IDCJob;
import com.iwellmass.idc.executor.IDCJobContext;

@Controller
public class IDCJobEndpoint {
	
	static final Logger LOGGER = LoggerFactory.getLogger(IDCJobEndpoint.class);

	Map<String, IDCJob> jobMap;
	
	@Resource
	IDCJobContextFactory contextFactory;
	
	@Resource(name = "idc-executor-")
	AsyncTaskExecutor executor;
	
	@PostMapping("/execute")
	public ServiceResult<String> execute(JobEnv jobEnv) {

		String type = jobEnv.getContentType();
		
		String key = type.toLowerCase();
		
		IDCJob job = jobMap.get(key);
		
		if (job == null) {
			throw new UnsupportedOperationException("不支持的 ContentType");
		} else {
			try {
				IDCJobContext context = contextFactory.newContext(jobEnv);
				doExecute(context);
				return ServiceResult.success("任务已提交");
			} catch (Exception e) {
				return ServiceResult.failure("任务已提交");
			}
		}
	}
	
	private void doExecute(IDCJobContext context) {

//		CompletableFuture.runAsync(() -> job.execute(context), executor)
//		.whenComplete((_void, cause) -> {
//			if (cause != null) {
//				CompleteEvent event = CompleteEvent.failureEvent(context.jobEnv.getInstanceId())
//					.setMessage("任务 {} 执行异常: {}", cause.getMessage())
//					.setEndTime(LocalDateTime.now());
//				context.complete(event);
//			}
//		});
	}
	
	
	@PostConstruct
	public void afterPropertiesSet(List<IDCJob> jobs) {
		jobMap = new HashMap<>();
		for (IDCJob job : jobs) {
			jobMap.put(job.toString(), job);
		}
	}
}
