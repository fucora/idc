package com.iwellmass.idc.quartz;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.quartz.utils.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.IDCServerConfiguration;

public class SpringConnectionProviderDelegate implements ConnectionProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionProvider.class);
	
	public DataSource dataSource;
	
	public SpringConnectionProviderDelegate() {
		this.dataSource = IDCServerConfiguration.dataSource();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void shutdown() throws SQLException {
		LOGGER.info("Quartz ConnectionProvider shutdown.");
	}

	@Override
	public void initialize() throws SQLException {
		LOGGER.info("Quartz ConnectionProvider initialize.");
	}

}
