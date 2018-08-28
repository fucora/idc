package com.iwellmass.idc;

import javax.inject.Inject;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.iwellmass.idc.repo")
@EntityScan("com.iwellmass.idc.model")
public class IDCServerConfiguration implements ApplicationListener<ContextRefreshedEvent>{

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCServerConfiguration.class);
	
	@Inject
	private Scheduler scheduler;

	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		if (event.getApplicationContext().getParent() == null) {
			try {
				LOGGER.info("启动调度器...");
				scheduler.startDelayed(5);
			} catch (SchedulerException e) {
				LOGGER.info("调度器启动失败", e);
			}
		}
		
	}
	
	
	
}
