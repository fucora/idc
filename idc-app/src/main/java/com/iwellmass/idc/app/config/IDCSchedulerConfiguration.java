package com.iwellmass.idc.app.config;

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

import com.iwellmass.idc.IDCLogger;
import com.iwellmass.idc.app.scheduler.IDCDriverDelegateImpl;
import com.iwellmass.idc.app.scheduler.IDCLoggerImpl;
import com.iwellmass.idc.app.scheduler.IDCPluginImpl;
import com.iwellmass.idc.app.scheduler.IDCPluginServiceImpl;
import com.iwellmass.idc.app.scheduler.JobFactoryImpl;
import com.iwellmass.idc.quartz.IDCDriverDelegate;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.quartz.IDCPluginService;
import com.iwellmass.idc.quartz.IDCSchedulerFactory;

@Configuration
public class IDCSchedulerConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCSchedulerConfiguration.class);
	
	@Value(value="${idc.scheduler.start-auto:true}")
	private Boolean startAuto;
	
	@Bean
	public IDCLogger idcLogger() {
		return new IDCLoggerImpl();
	}
	
	@Bean
	public IDCPluginService pluginService() {
		return new IDCPluginServiceImpl();
	}
	
	@Bean
	public IDCPlugin idcPlugin() {
		IDCPluginImpl plugin = new IDCPluginImpl(pluginService());
		plugin.setLogger(idcLogger());
		return plugin;
	}
	
	@Bean
	public IDCDriverDelegate idcDriverDelegate() {
		return new IDCDriverDelegateImpl();
	}
	
	@Bean
	public JobFactory jobFactory() {
		return new JobFactoryImpl();
	}
	
	@Bean
	public Scheduler scheduler(DataSource dataSource) throws SchedulerException {
		// 创建 scheduler
		IDCSchedulerFactory factory = new IDCSchedulerFactory();
		factory.setDataSource(dataSource);
		factory.setDriverDelegate(idcDriverDelegate());
		factory.setPlugin(idcPlugin());
		factory.setJobFactory(jobFactory());
		return factory.getScheduler();
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			Scheduler scheduler = event.getApplicationContext().getBean(Scheduler.class);
			if (startAuto && !scheduler.isStarted()) {
				LOGGER.info("启动IDCScheduler");
				scheduler.startDelayed(5);
			}
		} catch (SchedulerException e) {
			LOGGER.error("启动 IDCScheduler 失败: " + e.getMessage(), e);
		}
	}
}
