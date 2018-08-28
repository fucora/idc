package com.iwellmass.idc.server.quartz;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.TriggerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.SchedulerPlugin;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ApplicationObjectSupport;

import com.iwellmass.common.exception.AppException;

@Configuration
@SuppressWarnings("unused")
public class SpringQuartzBootstrap extends ApplicationObjectSupport {

	private static SpringQuartzBootstrap springContext;

	private ApplicationContext applicationContext;

	@Inject
	private DataSource dataSource; // new SpringConnectionProviderDelegate()
	@Inject // new SpringIDCPluginDelegate()
	private List<SchedulerPlugin> plugins;
	

	@Override
	protected void initApplicationContext(ApplicationContext context) throws BeansException {
		super.initApplicationContext(context);
		this.applicationContext = context;
	}

	@PostConstruct
	public void init() {
		springContext = this;
	}

	@Bean
	public Scheduler scheduler() throws SchedulerException {
		// 初始化 scheduler
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		//scheduler.startDelayed(5);
		return scheduler;
	}
	
	private static final SpringQuartzBootstrap ensuerInited() {
		if (springContext == null) {
			throw new IllegalStateException("spring context 尚未初始化");
		}
		return springContext;
	}

	public static final DataSource getDataSource() {
		return ensuerInited().dataSource;
	}

	public static SchedulerPlugin getSchedulerPlugin(String proxyClassName) {
		try {
			return (SchedulerPlugin) ensuerInited().applicationContext.getBean(Class.forName(proxyClassName));
		} catch (Exception e) {
			throw new AppException("无法初始化");
		}
	}
}
