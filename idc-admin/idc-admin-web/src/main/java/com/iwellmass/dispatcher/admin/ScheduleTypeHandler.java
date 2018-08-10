package com.iwellmass.dispatcher.admin;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.iwellmass.idc.model.ScheduleType;

public class ScheduleTypeHandler implements TypeHandler<ScheduleType>{

	@Override
	public void setParameter(PreparedStatement ps, int i, ScheduleType parameter, JdbcType jdbcType)
			throws SQLException {
		throw new UnsupportedOperationException("should never happen");
	}

	@Override
	public ScheduleType getResult(ResultSet rs, String columnName) throws SQLException {
		return fromDDC(rs.getString(columnName));
	}

	@Override
	public ScheduleType getResult(ResultSet rs, int columnIndex) throws SQLException {
		return fromDDC(rs.getString(columnIndex));
	}

	@Override
	public ScheduleType getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return fromDDC(cs.getString(columnIndex));
	}

	private static final ScheduleType fromDDC(String ddc) {
		return ddc == null || ddc.isEmpty() ? ScheduleType.MANUAL : ScheduleType.DAILY;
	}
}
