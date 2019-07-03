package com.iwellmass.idc.client.autoconfig;

import static com.iwellmass.idc.executor.IDCJobExecutorService.toURI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import com.iwellmass.idc.JobEnv;
import com.iwellmass.idc.executor.IDCJob;
import com.iwellmass.idc.executor.IDCJobContext;

@Configuration
@ConditionalOnBean(IDCJob.class)
@EnableAsync
@EnableFeignClients(basePackages="com.iwellmass.idc.client.autoconfig")
@EnableEurekaClient
public class IDCClientAutoConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCClientAutoConfiguration.class);

	@Bean(name = "idc-executor")
	public AsyncTaskExecutor asyncTaskExecutor() {
		return new SimpleAsyncTaskExecutor("idc-executor-");
	}

}
