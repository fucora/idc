package com.iwellmass.idc.autoconfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import com.iwellmass.idc.executor.IDCJob;

@Configuration
@ConditionalOnBean(IDCJob.class)
@EnableAsync
public class IDCClientAutoConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCClientAutoConfiguration.class);

	@Bean(name = "idc-executor")
	public AsyncTaskExecutor asyncTaskExecutor() {
		SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("idc-executor-");
		return executor;
	}

	@Bean
	public IDCJobHandlerMapping idcJobHandlerMapping(Map<String, IDCJob> idcJobs, AutowireCapableBeanFactory autowireFactory) {
		Map<String, IDCJobEndpoint> idcJobMap = new HashMap<>();
		for (Entry<String, IDCJob> idcJobEntry : idcJobs.entrySet()) {
			IDCJob job = idcJobEntry.getValue();
			IDCJobEndpoint delegate = new IDCJobEndpoint(job);
			autowireFactory.autowireBean(delegate);
			LOGGER.info("注册 IDCJob '{}' -> {} ", idcJobEntry.getKey(), job);
			idcJobMap.put("/idc-job/" + idcJobEntry.getKey(), delegate);
		}
		IDCJobHandlerMapping mapping = new IDCJobHandlerMapping();
		mapping.setDynamicControllerMap(idcJobMap);
		mapping.setOrder(Ordered.HIGHEST_PRECEDENCE - 100);
		return mapping;
	}
}
