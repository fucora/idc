package com.iwellmass.idc.app.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import com.iwellmass.idc.model.ScheduleStatus;

@MappedJdbcTypes(value= JdbcType.VARCHAR)
@MappedTypes(value = ScheduleStatus.class)
public class ScheduleStatusHandler implements TypeHandler<ScheduleStatus> {

	@Override
	public void setParameter(PreparedStatement ps, int i, ScheduleStatus parameter, JdbcType jdbcType)
			throws SQLException {
		throw new UnsupportedOperationException("not supported yet.");
	}

	@Override
	public ScheduleStatus getResult(ResultSet rs, String columnName) throws SQLException {
		String triggerState = rs.getString(columnName);
		return toScheduleStatus(triggerState);
	}

	@Override
	public ScheduleStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
		String triggerState = rs.getString(columnIndex);
		return toScheduleStatus(triggerState);
	}

	@Override
	public ScheduleStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String triggerState = cs.getString(columnIndex);
		return toScheduleStatus(triggerState);
	}
	
	
	private ScheduleStatus toScheduleStatus(String ts) {
		 if (ts == null) {
             return ScheduleStatus.NONE;
         }

         if (ts.equals("STATE_DELETED")) {
             return ScheduleStatus.NONE;
         }

         if (ts.equals("STATE_COMPLETE")) {
             return ScheduleStatus.COMPLETE;
         }

         if (ts.equals("STATE_PAUSED")) {
             return ScheduleStatus.PAUSED;
         }

         if (ts.equals("STATE_PAUSED_BLOCKED")) {
             return ScheduleStatus.PAUSED;
         }

         if (ts.equals("STATE_ERROR")) {
             return ScheduleStatus.ERROR;
         }

         if (ts.equals("STATE_BLOCKED")) {
             return ScheduleStatus.BLOCKED;
         }
         
         return ScheduleStatus.NORMAL;
	}

}
