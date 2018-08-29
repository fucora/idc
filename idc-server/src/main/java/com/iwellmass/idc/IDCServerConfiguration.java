package com.iwellmass.idc;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.iwellmass.idc.quartz.IDCPlugin;

@SpringBootApplication
@EnableJpaRepositories("com.iwellmass.idc.repo")
@EntityScan("com.iwellmass.idc.model")
public class IDCServerConfiguration implements ApplicationListener<ContextRefreshedEvent>{

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCServerConfiguration.class);
	
	private static IDCServerConfiguration INSTANCE;
	
	private Scheduler scheduler;
	
	@Inject
	private IDCPlugin idcPlugin;

	@Inject
	private DataSource dataSource;
	
	@Bean
	public Scheduler scheduler() throws SchedulerException {
		this.scheduler = StdSchedulerFactory.getDefaultScheduler();
		return scheduler;
	}

	@PostConstruct
	public void init() {
		INSTANCE = this;
	}
	
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
	
	public static SchedulerPlugin getIDCPlugin() {
		return ensureInited().idcPlugin;
	}

	public static DataSource getDataSource() {
		return ensureInited().dataSource;
	}
	
	public static IDCServerConfiguration ensureInited() {
		if (INSTANCE == null) {
			throw new IllegalAccessError("IDCPlugin 未能正常初始化, this is a bug :(");
		}
		return INSTANCE;
	}
	
}
