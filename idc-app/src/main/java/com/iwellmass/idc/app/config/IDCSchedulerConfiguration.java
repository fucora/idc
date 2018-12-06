package com.iwellmass.idc.app.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.IDCLogger;
import com.iwellmass.idc.IDCPluginService;
import com.iwellmass.idc.app.scheduler.IDCPluginImpl;
import com.iwellmass.idc.quartz.IDCDriverDelegate;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.quartz.IDCSchedulerFactory;

@Configuration
public class IDCSchedulerConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCSchedulerConfiguration.class);
	
	private Scheduler scheduler;
	
	@Value(value="${idc.scheduler.start-auto:true}")
	private Boolean startAuto;
	
	@Inject
	private JobFactory jobFactory;
	
	@Bean
	public IDCPlugin idcPlugin(IDCPluginService pluginService, DependencyService dependencyService, IDCLogger idcLogger) {
		IDCPluginImpl plugin = new IDCPluginImpl(pluginService, dependencyService);
		plugin.setLogger(idcLogger);
		return plugin;
	}
	
	@Bean
	public Scheduler scheduler(IDCDriverDelegate idcDriverDelegate, IDCLogger idcLogger,
			DataSource dataSource, IDCPluginService pluginService, DependencyService dependencyService) throws SchedulerException {
		// 创建 scheduler
		IDCSchedulerFactory factory = new IDCSchedulerFactory();
		factory.setDataSource(dataSource);
		factory.setDriverDelegate(idcDriverDelegate);
		factory.setPlugin(idcPlugin(pluginService, dependencyService, idcLogger));

		// 设置 scheduler 信息
		scheduler = factory.getScheduler();
		scheduler.setJobFactory(jobFactory);
		return scheduler;
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			if (startAuto && !scheduler.isStarted()) {
				LOGGER.info("启动IDCScheduler");
				scheduler.startDelayed(5);
			}
		} catch (SchedulerException e) {
			LOGGER.error("启动 IDCScheduler 失败: " + e.getMessage(), e);
		}
	}
}
