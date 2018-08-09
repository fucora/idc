package com.iwellmass.dispatcher.admin;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class MillsHandler implements TypeHandler<Timestamp>{

	@Override
	public void setParameter(PreparedStatement ps, int i, Timestamp parameter, JdbcType jdbcType) throws SQLException {
		ps.setLong(i, parameter.getTime());
	}

	@Override
	public Timestamp getResult(ResultSet rs, String columnName) throws SQLException {
		return new Timestamp(rs.getLong(columnName));
	}

	@Override
	public Timestamp getResult(ResultSet rs, int columnIndex) throws SQLException {
		return new Timestamp(rs.getLong(columnIndex));
	}

	@Override
	public Timestamp getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return new Timestamp(cs.getLong(columnIndex));
	}

}
