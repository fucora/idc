package com.iwellmass.idc.scheduler;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.iwellmass.idc.quartz.IDCContextKey;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.quartz.IDCPluginContext;
import com.iwellmass.idc.quartz.IDCSchedulerFactory;

@Configuration
@ComponentScan
@EnableJpaRepositories("com.iwellmass.idc.repo")
@EntityScan("com.iwellmass.idc.model")
public class IDCSchedulerConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCSchedulerConfiguration.class);

	private Scheduler scheduler;
	
	@Value(value="${idc.scheduler.start-auto:true}")
	private Boolean startAuto;
	
	@Bean
	public AutowireJobFactory idcJobFactory() {
		return new AutowireJobFactory();
	}

	@Bean
	public Scheduler scheduler(DataSource dataSource, IDCPluginContext pluginContext) throws SchedulerException {
		IDCPlugin.setDefaultContext(pluginContext);
		IDCSchedulerFactory factory = new IDCSchedulerFactory();
		factory.setDataSource(dataSource);
		
		scheduler = factory.getScheduler();
		scheduler.setJobFactory(idcJobFactory());
		return scheduler;
	}
	
	@Bean
	public IDCPlugin idcPlugin(Scheduler scheduler) throws SchedulerException {
		return IDCContextKey.IDC_PLUGIN.applyGet(scheduler.getContext());
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
