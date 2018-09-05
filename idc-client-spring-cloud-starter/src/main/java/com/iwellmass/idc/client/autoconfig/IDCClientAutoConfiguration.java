package com.iwellmass.idc.client.autoconfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import com.iwellmass.idc.executor.IDCJob;
import com.iwellmass.idc.executor.IDCJobExecutorService;

@Configuration
@ConditionalOnBean(IDCJob.class)
@EnableAsync
@EnableFeignClients
@EnableEurekaClient
public class IDCClientAutoConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCClientAutoConfiguration.class);

	@Inject
	public IDCStatusManagerClient idcStatusManagerClient;
	
	@Bean
	public AsyncService asyncService() {
		return new AsyncService();
	}
	
	@Bean
	public IDCExecutionContextFactory idcExecutionContextFactory() {
		return new IDCExecutionContextFactory();
	}

	@Bean(name = "idc-executor")
	public AsyncTaskExecutor asyncTaskExecutor() {
		SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("idc-executor-");
		return executor;
	}

	/*
	 * 发现 IDCJob，将其注册为 rest 资源，url 为 /{domain}/idc-job/{idc-job-name}/execution
	 * */
	@Bean
	public IDCJobHandlerMapping idcJobHandlerMapping(Map<String, IDCJob> idcJobs) {
		Map<String, IDCJobHandler> idcJobMap = new HashMap<>();
		for (Entry<String, IDCJob> idcJobEntry : idcJobs.entrySet()) {

			IDCJob job = idcJobEntry.getValue();
			IDCJobHandler jobHandler = new IDCJobHandler(job);
			jobHandler.setAsyncService(asyncService());
			jobHandler.setIdcStatusManagerClient(idcStatusManagerClient);
			jobHandler.setContextFactory(idcExecutionContextFactory());

			LOGGER.info("注册 IDCJob '{}' -> {} ", idcJobEntry.getKey(), job);
			String uri = MessageFormatter.arrayFormat(IDCJobExecutorService.RESOURCE_URI_TEMPLATE, 
					new Object[] {idcJobEntry.getKey()}).getMessage();
			
			idcJobMap.put(uri, jobHandler);
		}
		IDCJobHandlerMapping mapping = new IDCJobHandlerMapping();
		mapping.setDynamicControllerMap(idcJobMap);
		mapping.setOrder(Ordered.HIGHEST_PRECEDENCE - 100);
		return mapping;
	}
}
