package com.iwellmass.idc.app.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.TaskService;
import com.iwellmass.idc.app.service.JobServiceImpl;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

import com.iwellmass.idc.quartz.IDCDriverDelegate;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.quartz.IDCSchedulerFactory;

//@Configuration
//@ComponentScan
public class IDCSchedulerConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCSchedulerConfiguration.class);
	
	private Scheduler scheduler;
	
	@Value(value="${idc.scheduler.start-auto:true}")
	private Boolean startAuto;
	
	@Inject 
	private IDCDriverDelegate idcDriverDelegate;
	
	@Inject
	private JobFactory jobFactory;
	
	@Inject
	private IDCPlugin idcPlugin;

	@Inject
    private JobServiceImpl jobService;

	@Inject
    private TaskService taskService;

	@Inject
    private DependencyService dependencyService;
	
	@Bean
	public Scheduler scheduler(DataSource dataSource) throws SchedulerException {

		// 创建 scheduler
		IDCSchedulerFactory factory = new IDCSchedulerFactory();
		factory.setDriverDelegate(idcDriverDelegate);
		factory.setDataSource(dataSource);
		factory.setPlugin(idcPlugin);

        factory.setTaskService(taskService);
        factory.setJobService(jobService);
        factory.setDependencyService(dependencyService);

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
