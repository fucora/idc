package com.iwellmass.idc.scheduler.quartz;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.quartz.utils.ConnectionProvider;

public class NoManagedProvider implements ConnectionProvider {

	@Resource
	DataSource dataSource;
	
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