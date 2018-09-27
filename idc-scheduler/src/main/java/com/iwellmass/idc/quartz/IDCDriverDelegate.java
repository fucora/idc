package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCConstants.*;

import static org.quartz.TriggerKey.triggerKey;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;
import org.quartz.impl.jdbcjobstore.Util;

import com.iwellmass.idc.model.JobInstanceStatus;

public class IDCDriverDelegate extends StdJDBCDelegate {

    public static final String IDC_SELECT_NEXT_TRIGGER_TO_ACQUIRE = 
    		"SELECT "
	            + as("T", COL_TRIGGER_NAME) + ", " 
    			+ as("T", COL_TRIGGER_GROUP) + ", "
	            + as("T", COL_NEXT_FIRE_TIME) + ", " 
    			+ as("T", COL_PRIORITY) + ", "
    			+ "COUNT(" + as("D", COL_IDC_JOB_NAME) + ") DEP_CNT, "
    			+ "COUNT(" + as("I", COL_JOB_INSTANCE_STATUS) + ") FIN_CNT "
            + "FROM " 
    			+ TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " T "
            // ~~ 引入依赖表 ~~
            + "LEFT JOIN  " + TABLE_DEPENDENCY +" D " 
            	+ "ON "  + as("T", COL_JOB_NAME)   + " = " + as("D", COL_DEPENDENCY_SRC_JOB_NAME) + " "
            	+ "AND " + as("T", COL_JOB_GROUP)  + " = " + as("D", COL_DEPENDENCY_SRC_JOB_GROUP) + " "
            + "LEFT JOIN  " + TABLE_JOB_INSTANCE + " I "
            	+ "ON "    + as("D", COL_IDC_JOB_NAME)  + " = "   + as("I", COL_IDC_JOB_NAME)   + " "
            	+ "AND "   + as("D", COL_IDC_JOB_GROUP) + " = "   + as("I", COL_IDC_JOB_GROUP)  + " "
            	+ "AND "   + as("T", COL_NEXT_FIRE_TIME)       + " = "   + as("I", COL_JOB_INSTANCE_SHOULD_FIRE_TIME) + " "
            	+ "AND "   + as("I", COL_JOB_INSTANCE_STATUS)  + " = ? "
            + "WHERE " + as("T", COL_SCHEDULER_NAME) + " = " + SCHED_NAME_SUBST + " "
	            + "AND " + as("T", COL_TRIGGER_STATE) + " = ? AND " + as("T", COL_NEXT_FIRE_TIME) + " <= ? " 
	            + "AND (" + as("T", COL_MISFIRE_INSTRUCTION) + " = -1 OR (" +as("T", COL_MISFIRE_INSTRUCTION)+ " != -1 AND "+ as("T", COL_NEXT_FIRE_TIME) + " >= ?)) "
            // ~~ 引入依赖表 ~~
            + "GROUP BY "
            	+ as("T", COL_TRIGGER_NAME) + ", " 
    			+ as("T", COL_TRIGGER_GROUP) + ", "
	            + as("T", COL_NEXT_FIRE_TIME) + ", " 
    			+ as("T", COL_PRIORITY) + " "
    		+ "HAVING DEP_CNT = FIN_CNT "
            + "ORDER BY "+ as("T", COL_NEXT_FIRE_TIME) + " ASC, " + as("T", COL_PRIORITY) + " DESC";
	
	@Override
	public List<TriggerKey> selectTriggerToAcquire(Connection conn, long noLaterThan, long noEarlierThan, int maxCount)
			throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TriggerKey> nextTriggers = new LinkedList<>();
        try {
            ps = conn.prepareStatement(rtp(IDC_SELECT_NEXT_TRIGGER_TO_ACQUIRE));
            
            // Set max rows to retrieve
            if (maxCount < 1)
                maxCount = 1; // we want at least one trigger back.
            ps.setMaxRows(maxCount);
            
            // Try to give jdbc driver a hint to hopefully not pull over more than the few rows we actually need.
            // Note: in some jdbc drivers, such as MySQL, you must set maxRows before fetchSize, or you get exception!
            ps.setFetchSize(maxCount);
            
            ps.setInt(1, JobInstanceStatus.FINISHED.ordinal());
            ps.setString(2, STATE_WAITING);
            ps.setBigDecimal(3, new BigDecimal(String.valueOf(noLaterThan)));
            ps.setBigDecimal(4, new BigDecimal(String.valueOf(noEarlierThan)));
            logger.info("noLaterThan:"+noLaterThan+",noEarlierThan"+noEarlierThan);
            rs = ps.executeQuery();
            
            while (rs.next() && nextTriggers.size() <= maxCount) {
                nextTriggers.add(triggerKey(
                        rs.getString(COL_TRIGGER_NAME),
                        rs.getString(COL_TRIGGER_GROUP)));
            }
            
            return nextTriggers;
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
        }      
    }
	
	public static  String  as(String alias, String name) {
		return alias + "." + name;
	}
	
	public static void main(String[] args) {
		
		String msg = Util.rtp(IDC_SELECT_NEXT_TRIGGER_TO_ACQUIRE, "QRTZ_", "IDCScheduler");
		
		System.out.println(msg);
		
	}
}
