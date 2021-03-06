package com.iwellmass.idc.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;

import com.iwellmass.idc.scheduler.quartz.*;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.scheduler.service.IDCJobExecutor;

@Configuration
@ComponentScan
@EnableJpaRepositories("com.iwellmass.idc.scheduler.repository")
@EntityScan("com.iwellmass.idc.scheduler.model")
public class SchedulerConfig {

	static final Logger LOGGER = LoggerFactory.getLogger(SchedulerConfig.class);

	public static boolean clearBeforStart = Boolean.valueOf(System.getProperty("idc.scheduler.clearBeforStart", "false"));

	@Value(value = "${idc.scheduler.start-auto:true}")
	private Boolean startAuto;
	@Value(value = "${idc.scheduler.openCallbackControl:false}")
	boolean openCallbackControl;
	@Value(value = "${idc.scheduler.maxRunningJobs:10}")
	private Integer maxRunningJobs;
	@Value(value = "${idc.scheduler.callbackTimeout:1800}")
	Long timeout;
	@Value(value = "${idc.scheduler.retryCount:3}")
	private Integer retryCount;

	@Resource
	DataSource dataSource;

	public RecordIdGenerator recordIdGenerator() {
		AtomicLong seq = new AtomicLong();
		return () -> String.valueOf(seq.incrementAndGet());
	}
	
	@Bean
	public ConnectionProvider managedProvider() {
		return new ManagedProvider();
	}
	
	@Bean
	public ConnectionProvider noManagedProvider() {
		return new NoManagedProvider();
	}
	
	@Bean
	public IDCJobstoreCMT idcJobStore() {

		DBConnectionManager.getInstance().addConnectionProvider("ds1", managedProvider());
		DBConnectionManager.getInstance().addConnectionProvider("ds2", noManagedProvider());

		IDCJobstoreCMT cmt = new IDCJobstoreCMT();
		cmt.setDataSource("ds1");
		cmt.setNonManagedTXDataSource("ds2");
		// cmt.setRecordIdGenerator(recordIdGenerator());
		cmt.setDontSetAutoCommitFalse(true);
		return cmt;
	}
	
	@Bean
	public TaskEventPlugin taskEventPlugin() {
		return new TaskEventPlugin();
	}
	
	@Bean
	public Scheduler scheduler() {
		return IDCSchedulerFactory.getScheduler(taskEventPlugin(), idcJobStore());
	}

	@EventListener(ApplicationReadyEvent.class)
	public void autoStart(ApplicationReadyEvent event) throws SchedulerException {
		// set default executor
		IDCJobExecutor executor = event.getApplicationContext().getBean(IDCJobExecutor.class);
		IDCJobExecutors.setGlobalExecutor(executor);
		// clear before
		if (clearBeforStart) {
			LOGGER.info("clear scheduler...");
			scheduler().clear();
		}
		// start scheduler
		if (startAuto) {
			LOGGER.info("是否开启回调超时：" + openCallbackControl);
			if (openCallbackControl) {
				LOGGER.info("回调超时时长（秒）：" + timeout);
			}
			LOGGER.info("最大并发数：" + maxRunningJobs);
			LOGGER.info("失败重试次数：" + retryCount);

			scheduler().start();
		}
	}

}
