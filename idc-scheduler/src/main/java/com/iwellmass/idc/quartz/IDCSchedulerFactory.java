package com.iwellmass.idc.quartz;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.jdbcjobstore.InvalidConfigurationException;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.SchedulerPlugin;
import org.quartz.spi.ThreadPool;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 创建 Scheduler 单例
 */
public final class IDCSchedulerFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IDCSchedulerFactory.class);
	
	private static final String SCHED_ID = "IDC";
	private static final String SCHED_NAME = "IDCScheduler";
	
	private boolean inited = false;

	private int threadCount = 4;
	private DataSource dataSource;
	private String idcDriverDelegateClass;
	
	public Scheduler getScheduler() throws SchedulerException {
		if (!inited) {
			LOGGER.info("创建 {}...", SCHED_NAME);
			// threadPool
			SimpleThreadPool threadPool = new SimpleThreadPool();
			threadPool.setInstanceId(SCHED_ID);
			threadPool.setInstanceName(SCHED_NAME);
			threadPool.setThreadCount(threadCount);
			threadPool.setThreadNamePrefix("idc-");
			try {
				createScheduler(threadPool);
				inited = true;
			} catch (SchedulerException e) {
				threadPool.shutdown(false);
				throw e;
			} catch (Throwable e) {
				threadPool.shutdown(false);
				throw new SchedulerException("创建调度器失败: " + e.getMessage(), e);
			}
		}
		return DirectSchedulerFactory.getInstance().getScheduler(SCHED_NAME);
	}
	
	private void createScheduler(ThreadPool threadPool) throws SchedulerException {
		
		String dsName = "idc";
		
		// db
		DBConnectionManager.getInstance().addConnectionProvider(dsName, new SimpleConnectionProvider());
		
		// JobStroe
		IDCJobStoreTX jobStore = new IDCJobStoreTX();
		jobStore.setInstanceId(SCHED_ID);
		jobStore.setInstanceName(SCHED_NAME);
		jobStore.setDataSource(dsName);
		
		if (idcDriverDelegateClass ==  null) {
			throw new NullPointerException("IDCDriverDelegate not set");
		}
		
		try {
			jobStore.setDriverDelegateClass(idcDriverDelegateClass);
		} catch (InvalidConfigurationException e) {
			throw new SchedulerException("初始化 JobStore 时出错", e);
		}
		
		// IDCPlugin
		Map<String, SchedulerPlugin> schedulerPluginMap = new HashMap<>();
		schedulerPluginMap.put(IDCPlugin.class.getSimpleName(), new IDCPlugin(jobStore));
		DirectSchedulerFactory.getInstance().createScheduler(SCHED_NAME, SCHED_ID, threadPool, jobStore, schedulerPluginMap, null, 0, -1, -1, false, null);
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public String getIdcDriverDelegateClass() {
		return idcDriverDelegateClass;
	}

	public void setIdcDriverDelegateClass(String idcDriverDelegateClass) {
		this.idcDriverDelegateClass = idcDriverDelegateClass;
	}




	class SimpleConnectionProvider implements ConnectionProvider {

		@Override
		public Connection getConnection() throws SQLException {
			return dataSource.getConnection();
		}

		@Override
		public void shutdown() throws SQLException {
		}

		@Override
		public void initialize() throws SQLException {
		}
	}
}
