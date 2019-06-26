package com.iwellmass.idc.scheduler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.PropertySettingJobFactory;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.SchedulerPlugin;
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
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.scheduler.quartz.IDCJobstoreCMT;
import com.iwellmass.idc.scheduler.quartz.RecordIdGenerator;

@Configuration
@ComponentScan
@EnableJpaRepositories("com.iwellmass.idc.scheduler.repository")
@EntityScan("com.iwellmass.idc.scheduler.model")
public class SchedulerConfig {

	static final Logger LOGGER = LoggerFactory.getLogger(SchedulerConfig.class);

	public static boolean clear = false;
	
	@Value(value = "${idc.scheduler.start-auto:true}")
	private Boolean startAuto;
	

	@Resource
	DataSource dataSource;
	
	public RecordIdGenerator recordIdGenerator() {
		AtomicLong seq = new AtomicLong();
		return () -> String.valueOf(seq.incrementAndGet());
	}
	
	@Bean
	public IDCJobstoreCMT idcJobStore() {

		DBConnectionManager.getInstance().addConnectionProvider("ds1", new ManagedProvider());
		DBConnectionManager.getInstance().addConnectionProvider("ds2", new NoManagedProvider());

		IDCJobstoreCMT cmt = new IDCJobstoreCMT();
		cmt.setDataSource("ds1");
		cmt.setNonManagedTXDataSource("ds2");
		cmt.setRecordIdGenerator(recordIdGenerator());
		return cmt;
	}
	
	@Bean
	public TaskEventPlugin taskEventPlugin() {
		return new TaskEventPlugin();
	}
	
	@Bean
	public Scheduler scheduler() throws SchedulerException {

		String schedulerName = "idc-schd";
		String schedulerInstanceId = "idc-schd-01";
		String rmiRegistryHost = null;
		int rmiRegistryPort = 0;
		int idleWaitTime = -1;
		int dbFailureRetryInterval = -1;
		boolean jmxExport = false;
		String jmxObjectName = null;

		SimpleThreadPool threadPool = new SimpleThreadPool();
		threadPool.setThreadCount(1);
		threadPool.setThreadNamePrefix("qw-");

		Map<String, SchedulerPlugin> schedulerPluginMap = new HashMap<>();
		schedulerPluginMap.put(TaskEventPlugin.NAME, taskEventPlugin());
		
		DirectSchedulerFactory.getInstance().createScheduler(schedulerName, schedulerInstanceId, threadPool, idcJobStore(), schedulerPluginMap,
			rmiRegistryHost, rmiRegistryPort, idleWaitTime, dbFailureRetryInterval, jmxExport, jmxObjectName);
		
		Scheduler scheduler = DirectSchedulerFactory.getInstance().getScheduler(schedulerName);
		scheduler.setJobFactory(new PropertySettingJobFactory());
		
		if (clear) {
			scheduler.clear();
		}
		// export
		return scheduler;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void autoStart() throws SchedulerException {
		if (startAuto) {
			 // scheduler().start();
		}
	}
	
	class NoManagedProvider implements ConnectionProvider {

		@Override
		public Connection getConnection() throws SQLException {
			Connection conn = dataSource.getConnection();
			return conn;
		}

		@Override
		public void shutdown() throws SQLException {
		}

		@Override
		public void initialize() throws SQLException {
		}

	}

	class ManagedProvider implements ConnectionProvider {

		@Override
		public Connection getConnection() throws SQLException {
			Connection conn = DataSourceUtils.getConnection(dataSource);
			return conn;
		}

		@Override
		public void shutdown() throws SQLException {
		}

		@Override
		public void initialize() throws SQLException {
		}
	}

}
