package com.iwellmass.dispatcher.admin;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.iwellmass.idc.model.JobInstanceType;

public class JobInstanceTypeHandler implements TypeHandler<JobInstanceType> {

	public static final int[] values = new int[] { 0, 1, 0x10 };

	@Override
	public void setParameter(PreparedStatement ps, int i, JobInstanceType parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setInt(i, asDDCTriggerType(parameter));
	}

	@Override
	public JobInstanceType getResult(ResultSet rs, String columnName) throws SQLException {
		return ofDDCTriggerType(rs.getInt(columnName));
	}

	@Override
	public JobInstanceType getResult(ResultSet rs, int columnIndex) throws SQLException {
		return ofDDCTriggerType(rs.getInt(columnIndex));
	}

	@Override
	public JobInstanceType getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return ofDDCTriggerType(cs.getInt(columnIndex));
	}

	public static final JobInstanceType ofDDCTriggerType(int i) {

		// public static final int TASK_TRIGGER_TYPE_SYSTEM = 0;
		// public static final int TASK_TRIGGER_TYPE_MAN = 1;
		// public static final int TASK_TRIGGER_TYPE_MAN_COMPLEMENT = 0x10;
		// @ApiModelProperty("周期实例")
		// CRON,
		// TASK_TRIGGER_TYPE_SYSTEM = 0;
		// @ApiModelProperty("手动实例")
		// MANUAL,
		// TASK_TRIGGER_TYPE_MAN = 1;
		// @ApiModelProperty("补数实例")
		// COMPLEMENT;
		// TASK_TRIGGER_TYPE_MAN_COMPLEMENT = 0x10;
		if (i == 0) {
			return JobInstanceType.CRON;
		} else if (i == 1) {
			return JobInstanceType.MANUAL;
		} else if (i == 0x10) {
			return JobInstanceType.COMPLEMENT;
		}
		return JobInstanceType.CRON;
	}
	public static final int asDDCTriggerType(JobInstanceType type) {
		return values[type.ordinal()];
	}
}
