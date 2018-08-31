package com.iwellmass.idc.executor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

@Configuration
@EnableAsync
public class IDCClientAutoConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCClientAutoConfiguration.class);

	@Bean(name = "idc-executor")
	public AsyncTaskExecutor asyncTaskExecutor() {
		SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("idc-executor-");
		return executor;
	}

	@Bean
	public SimpleUrlHandlerMapping simpleUrlHandlerMapping(List<IDCJob> idcJobs, AutowireCapableBeanFactory acbf) {

		LOGGER.info("注册 IDCJob...");
		Map<String, HandlerMethod> urlMap = new HashMap<>();
		for (IDCJob job : idcJobs) {
			IDCJobDelegate delegate = new IDCJobDelegate(job);
			acbf.autowireBean(delegate);
			Map<String, HandlerMethod> m = createUrlMapping("/idc/", delegate);
			if (m.isEmpty()) {
				LOGGER.warn("未找到 {} 可用的方法", job.getClass().getSimpleName());
			} else {
				m.forEach((k, v) -> {
					LOGGER.info("注册 {} -> {}", k, job.getClass().getSimpleName());
				});
				urlMap.putAll(m);
			}
		}

		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		mapping.setUrlMap(urlMap);
		mapping.setOrder(Ordered.HIGHEST_PRECEDENCE - 100);
		return mapping;
	}

	public Map<String, HandlerMethod> createUrlMapping(String prefix, IDCJobDelegate controller) {
		Map<String, HandlerMethod> map = new HashMap<>();
		Method[] methods = IDCJobDelegate.class.getMethods();
		for (Method method : methods) {
			if (method.getDeclaringClass() == controller.getClass()) {
				HandlerMethod hm = new HandlerMethod(controller, method);
				map.put(prefix + "/" + method.getName(), hm);
			}
		}
		return map;
	}
}
