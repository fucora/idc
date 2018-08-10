package com.iwellmass.dispatcher.admin;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.iwellmass.dispatcher.thrift.bvo.TaskTypeHelper;
import com.iwellmass.idc.model.ContentType;

public class ContentTypeHandler implements TypeHandler<ContentType> {

	@Override
	public void setParameter(PreparedStatement ps, int i, ContentType parameter, JdbcType jdbcType)
			throws SQLException {
		String className = TaskTypeHelper.classNameOf(parameter.toString());
		ps.setString(i, className);
	}

	@Override
	public ContentType getResult(ResultSet rs, String columnName) throws SQLException {
		String className = rs.getString(columnName);
		return ofDDC(className);
	}

	@Override
	public ContentType getResult(ResultSet rs, int columnIndex) throws SQLException {
		String className = rs.getString(columnIndex);
		return ofDDC(className);
	}

	@Override
	public ContentType getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String className = cs.getString(columnIndex);
		return ofDDC(className);
	}

	private static final ContentType ofDDC(String value) {

		if (value == null) {
			return null;
		}
		try {
			String contentType = TaskTypeHelper.contentTypeOf(value);
			return ContentType.valueOf(contentType);
		} catch (Throwable e) {
			return null;
		}

	}
}
