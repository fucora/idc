package com.iwellmass.dispatcher.admin;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.iwellmass.idc.model.TaskType;

public class TaskTypeByWorkflowIdHandler implements TypeHandler<TaskType> {

	// 任务类型（11:定时任务 12:流程子任务 13:简单任务 21:流程任务）
	// TaskType.NODE_TASK;
	// TaskType.WORKFLOW;
	// TaskType.WORKFLOW_TASK;
	public static final int[] values = new int[] { 11, 21, 12 };

	@Override
	public void setParameter(PreparedStatement ps, int i, TaskType parameter, JdbcType jdbcType) throws SQLException {
		ps.setInt(i, values[parameter.ordinal()]);
	}

	@Override
	public TaskType getResult(ResultSet rs, String columnName) throws SQLException {
		int ddcTaskType = rs.getInt(columnName);
		return ofDDCTaskType(ddcTaskType);
	}

	@Override
	public TaskType getResult(ResultSet rs, int columnIndex) throws SQLException {
		int ddcTaskType = rs.getInt(columnIndex);
		return ofDDCTaskType(ddcTaskType);
	}

	@Override
	public TaskType getResult(CallableStatement cs, int columnIndex) throws SQLException {
		int ddcTaskType = cs.getInt(columnIndex);
		return ofDDCTaskType(ddcTaskType);
	}

	private static final TaskType ofDDCTaskType(int ddcTaskType) {
		if (ddcTaskType < 0) {
			return TaskType.WORKFLOW_TASK;
		} else if (ddcTaskType > 0) {
			return TaskType.WORKFLOW;
		} else {
			return TaskType.NODE_TASK;
		}
	}

}
