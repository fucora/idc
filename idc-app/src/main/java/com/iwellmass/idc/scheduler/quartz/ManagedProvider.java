package com.iwellmass.idc.scheduler.quartz;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.quartz.utils.ConnectionProvider;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class ManagedProvider implements ConnectionProvider {

	@Resource
	DataSource dataSource;

	public Connection getConnection() throws SQLException {
		Connection conn = DataSourceUtils.doGetConnection(dataSource);
		return conn;
	}

	@Override
	public void shutdown() throws SQLException {
	}

	@Override
	public void initialize() throws SQLException {
	}
}