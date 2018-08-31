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

import com.iwellmass.idc.model.SentinelStatus;

public class IDCDriverDelegate extends StdJDBCDelegate{

	public static final String  IDC_SELECT_NEXT_TRIGGER_TO_ACQUIRE = "SELECT "
            + alias("T", COL_TRIGGER_NAME) + ", " + alias("T", COL_TRIGGER_GROUP) + ", "
            + alias("T", COL_NEXT_FIRE_TIME) + ", " + alias("T", COL_PRIORITY) + " FROM "
            + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " T"
            + " LEFT JOIN " + TABLE_SENTINEL + " S" 
            + " ON "  + alias("T", COL_TRIGGER_NAME) + "=" + alias("S", COL_SENTINEL_TRIGGER_NAME)
            + " AND " + alias("T", COL_TRIGGER_GROUP) + "=" + alias("S", COL_SENTINEL_TRIGGER_GROUP)
            + " AND " + alias("T", COL_NEXT_FIRE_TIME) + "=" + alias("S", COL_SENTINEL_SHOULD_FIRE_TIME)
            + " WHERE " + alias("T", COL_SCHEDULER_NAME) + " = " + SCHED_NAME_SUBST
            + " AND " + alias("T", COL_TRIGGER_STATE) + " = ? AND " + alias("T", COL_NEXT_FIRE_TIME) + " <= ? " 
            + "AND (" + alias("T", COL_MISFIRE_INSTRUCTION) + " = -1 OR (" +alias("T", COL_MISFIRE_INSTRUCTION) + " != -1 AND "+ alias("T", COL_NEXT_FIRE_TIME) + " >= ?)) "
            + "AND " + alias("S", COL_SENTINEL_STATUS) + " = ? "            
            + "ORDER BY "+ alias("T", COL_NEXT_FIRE_TIME) + " ASC, " + alias("T", COL_PRIORITY) + " DESC";
	
	@Override
	public List<TriggerKey> selectTriggerToAcquire(Connection conn, long noLaterThan, long noEarlierThan, int maxCount)
			throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TriggerKey> nextTriggers = new LinkedList<TriggerKey>();
        try {
            ps = conn.prepareStatement(rtp(IDC_SELECT_NEXT_TRIGGER_TO_ACQUIRE));
            
            // Set max rows to retrieve
            if (maxCount < 1)
                maxCount = 1; // we want at least one trigger back.
            ps.setMaxRows(maxCount);
            
            // Try to give jdbc driver a hint to hopefully not pull over more than the few rows we actually need.
            // Note: in some jdbc drivers, such as MySQL, you must set maxRows before fetchSize, or you get exception!
            ps.setFetchSize(maxCount);
            
            ps.setString(1, STATE_WAITING);
            ps.setBigDecimal(2, new BigDecimal(String.valueOf(noLaterThan)));
            ps.setBigDecimal(3, new BigDecimal(String.valueOf(noEarlierThan)));
            ps.setInt(4, SentinelStatus.READY.ordinal());
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
	
	private static final String alias(String alias, String name) {
		return alias + "." + name;
	}
	public static void main(String[] args) {
		String query = org.quartz.impl.jdbcjobstore.Util.rtp(IDC_SELECT_NEXT_TRIGGER_TO_ACQUIRE, "QRTZ_", "'abc'");
		System.out.println(query);
	}
	
}
