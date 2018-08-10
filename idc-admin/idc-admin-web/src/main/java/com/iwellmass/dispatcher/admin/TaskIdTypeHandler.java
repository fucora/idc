package com.iwellmass.dispatcher.admin;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TaskIdTypeHandler implements TypeHandler<String>{

	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		throw new UnsupportedOperationException("should never happen");
	}

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {
		String parameters = rs.getString(columnName);
		return ofDDCTask(parameters);
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {
		String parameters = rs.getString(columnIndex);
		return ofDDCTask(parameters);
	}

	@Override
	public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String parameters = cs.getString(columnIndex);
		return ofDDCTask(parameters);
	}

	private static final String ofDDCTask(String parameters) {
		if (parameters == null) {
			return null;
		}
		try {
			JSONObject jo = JSON.parseObject(parameters).getJSONObject("task");
			return jo.getString("taskId");
		} catch (Throwable e) {
			return null;
		}
		
	}
}
