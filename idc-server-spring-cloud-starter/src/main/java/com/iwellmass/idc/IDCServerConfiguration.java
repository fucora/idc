package com.iwellmass.idc;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.quartz.IDCQuartzJobFactory;

@Configuration
@ComponentScan
@EnableJpaRepositories("com.iwellmass.idc.repo")
@EntityScan("com.iwellmass.idc.model")
public class IDCServerConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCServerConfiguration.class);

	private static IDCServerConfiguration INSTANCE;

	private DataSource dataSource;

	// ~~ IDCPlugin component~~
	private IDCPlugin idcPlugin;

	private Scheduler scheduler;

	@Bean
	public IDCQuartzJobFactory idcJobFactory() {
		return new IDCQuartzJobFactory();
	}

	@Bean
	public Scheduler scheduler(DataSource dataSource, IDCPlugin idcPlugin) throws SchedulerException {
		this.dataSource = dataSource;
		this.idcPlugin = idcPlugin;
		this.scheduler = StdSchedulerFactory.getDefaultScheduler();
		// 托管 job
		this.scheduler.setJobFactory(idcJobFactory());

		return scheduler;
	}

	@PostConstruct
	public void init() {
		INSTANCE = this;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			LOGGER.info("启动调度器...");
			scheduler.startDelayed(5);
		} catch (SchedulerException e) {
			LOGGER.info("调度器启动失败", e);
		}
	}

	public static SchedulerPlugin idcPlugin() {
		return ensureInited().idcPlugin;
	}

	public static DataSource dataSource() {
		return ensureInited().dataSource;
	}

	public static IDCServerConfiguration ensureInited() {
		if (INSTANCE == null) {
			throw new IllegalAccessError("IDCPlugin 未能正常初始化, this is a bug :(");
		}
		return INSTANCE;
	}

}
