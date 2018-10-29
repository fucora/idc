package com.iwellmass.idc.quartz;

import static org.quartz.TriggerKey.triggerKey;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;
import org.quartz.impl.jdbcjobstore.Util;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.BarrierState;

public class IDCDriverDelegate extends StdJDBCDelegate implements IDCConstants {

    public static final String IDC_SELECT_NEXT_TRIGGER_TO_ACQUIRE = "SELECT "
	        + "T." + COL_TRIGGER_NAME + ", T." + COL_TRIGGER_GROUP + ", "
	        + "T." + COL_NEXT_FIRE_TIME + ", T." + COL_PRIORITY + " FROM "
	        + TABLE_PREFIX_SUBST + TABLE_TRIGGERS + " T"
	        + " LEFT JOIN " + TABLE_PREFIX_SUBST + TABLE_BARRIER + " B ON T." + COL_TRIGGER_NAME + " = B." + COL_TRIGGER_NAME
	        + " AND T." + COL_TRIGGER_GROUP + " = B." + COL_TRIGGER_GROUP + " AND B." + COL_BARRIER_STATE + " = " + BarrierState.VALID.ordinal()
	        + " WHERE "
	        + COL_SCHEDULER_NAME + " = " + SCHED_NAME_SUBST
	        + " AND " + COL_TRIGGER_STATE + " = ? AND " + COL_NEXT_FIRE_TIME + " <= ? " 
	        + "AND (" + COL_MISFIRE_INSTRUCTION + " = -1 OR (" +COL_MISFIRE_INSTRUCTION+ " != -1 AND "+ COL_NEXT_FIRE_TIME + " >= ?)) "
	        + "AND B." + COL_TRIGGER_NAME + " IS NULL "
	        + "ORDER BY "+ COL_NEXT_FIRE_TIME + " ASC, " + COL_PRIORITY + " DESC";
    
    public static final String IDC_UPDATE_JOB_BARRIER_STATE = "UPDATE QRTZ_BARRIER "
    		+ "SET STATE = ? WHERE BARRIER_NAME = ? AND BARRIER_GROUP = ? AND BARRIER_SHOULD_FIRE_TIME = ?";
    
    public static final String INSERT_BARRIER = "INSERT INTO " + TABLE_BARRIER + "(" + COL_TRIGGER_NAME + ", "
			+ COL_TRIGGER_GROUP + ", " + COL_BARRIER_NAME + ", " + COL_BARRIER_GROUP + ", " + COL_BARRIER_SHOULD_FIRE_TIME
			+ ", " + COL_BARRIER_STATE + ") " + "VALUES (?, ?, ?, ?, ?, ?);";
	
    public static final String DELETE_BARRIER = "DELETE FROM " + TABLE_BARRIER + " WHERE " + COL_IDC_JOB_NAME + " = ? AND "
			+ COL_IDC_JOB_GROUP + " = ? ";
    
    
    // WAITING --> ACQUIRED
	@Override
	public List<TriggerKey> selectTriggerToAcquire(Connection conn, long noLaterThan, long noEarlierThan, int maxCount)
			throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TriggerKey> nextTriggers = new LinkedList<>();
        try {
        	
        	////////////////////////////////////////////////////////////////////////////
        	// 加入 barrier 判断
        	// ps = conn.prepareStatement(rtp(SELECT_NEXT_TRIGGER_TO_ACQUIRE));
        	////////////////////////////////////////////////////////////////////////////
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
	
	// INSERT BARRIER
	public int insertBarrier(Connection conn) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(rtp(INSERT_BARRIER));
			return ps.executeUpdate();
		} finally {
			closeStatement(ps);
		}   
	}
	
	// DELETE BARRIER
	
	public int deleteBarrier(Connection conn) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(rtp(DELETE_BARRIER));
			return ps.executeUpdate();
		} finally {
			closeStatement(ps);
		}   
	}
	
	// ASYNC CALL: BLOCKED --> WAITING
	public int updateTriggerStateForIDC(Connection conn, CompleteEvent event) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(IDC_UPDATE_JOB_BARRIER_STATE);
			ps.setInt(1, event.getFinalStatus().ordinal()); // status
			ps.setTimestamp(2, new Timestamp(IDCPlugin.toEpochMilli(event.getEndTime()))); // 时间
			ps.setInt(3, event.getInstanceId());
			return ps.executeUpdate();
		} finally {
			closeStatement(ps);
		}   
	}
	
	
	public static void main(String[] args) {
		String msg = Util.rtp(IDC_SELECT_NEXT_TRIGGER_TO_ACQUIRE, "QRTZ_", "IDCScheduler");
		System.out.println(msg);
	}
}
