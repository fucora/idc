package com.iwellmass.idc.quartz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.iwellmass.idc.model.BarrierState;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

public class SimpleIDCDriverDelegate implements IDCDriverDelegate, IDCConstants {

	private static final Map<Integer, JobInstance> jobInstanceMap = new HashMap<>();
	
	public static final String IDC_INSERT_JOB_BARRIER = "INSERT INTO " + TABLE_BARRIER + "(" + COL_IDC_JOB_NAME + ", "
			+ COL_IDC_JOB_GROUP + ", " + COL_BARRIER_NAME + ", " + COL_BARRIER_GROUP + ", " + COL_BARRIER_SHOULD_FIRE_TIME
			+ ", " + COL_BARRIER_STATE + ") " + "VALUES (?, ?, ?, ?, ?, ?);";
	
    public static final String IDC_UPDATE_JOB_BARRIER = "DELETE FROM QRTZ_" + TABLE_BARRIER + " WHERE " + COL_IDC_JOB_NAME + " = ? AND "
			+ COL_IDC_JOB_GROUP + " = ? AND " + COL_BARRIER_SHOULD_FIRE_TIME + " = ?";
    
    public static final String IDC_CLEAR_JOB_BARRIER = "DELETE FROM " + TABLE_BARRIER + " WHERE " + COL_IDC_JOB_NAME + " = ? AND "
    		+ COL_IDC_JOB_GROUP + " = ?";
    public static final String IDC_CLEAR_ALL_JOB_BARRIER = "DELETE FROM " + TABLE_BARRIER;
	
	@Override
	public JobInstance updateJobInstance(Connection conn, Integer instanceId, Consumer<JobInstance> func) throws SQLException {
		JobInstance ins = selectJobInstance(conn, instanceId);
		func.accept(ins);
		return ins;
	}

	@Override
	public JobInstance selectJobInstance(Connection conn, Integer instanceId) throws SQLException {
		JobInstance ins = jobInstanceMap.get(instanceId);
		return ins;
	}
	
	@Override
	public JobInstance selectJobInstance(Connection conn, JobKey jobKey, long shouldFireTime) {
		Optional<JobInstance> aa = jobInstanceMap.values().stream().filter(ins -> ins.getJobKey().equals(jobKey) && ins.getShouldFireTime() == shouldFireTime)
				.findFirst();
		return aa.orElse(null);
	}

	@Override
	public JobInstance insertJobInstance(Connection conn, JobInstance job) {
		synchronized (jobInstanceMap) {
			jobInstanceMap.put(job.getInstanceId(), job);
			return job;
		}
	}

	@Override
	public void batchInsertJobBarrier(Connection conn, List<JobBarrier> barriers) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(IDC_INSERT_JOB_BARRIER);
			for (JobBarrier barrier : barriers) {
				ps.clearParameters();
				ps.setString(1, barrier.getJobId());
				ps.setString(2, barrier.getJobGroup());
				ps.setString(3, barrier.getBarrierId());
				ps.setString(4, barrier.getBarrierGroup());
				ps.setLong(5, barrier.getBarrierShouldFireTime());
				ps.setInt(6, BarrierState.VALID.ordinal());
				ps.addBatch();
			}
			ps.executeBatch();
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}


	@Override
	public void disableBarriers(Connection conn, String jobId, String jobGroup, Long shouldFireTime) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(IDC_UPDATE_JOB_BARRIER);
			ps.clearParameters();
			ps.setString(1, jobId);
			ps.setString(2, jobGroup);
			ps.setLong(3, shouldFireTime);
			ps.executeUpdate();
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	@Override
	public void clearJobBarrier(Connection conn, JobKey jobKey) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(IDC_CLEAR_JOB_BARRIER);
			ps.clearParameters();
			ps.setString(1, jobKey.getJobId());
			ps.setString(2, jobKey.getJobGroup());
			ps.executeUpdate();
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	@Override
	public void clearAllBarrier(Connection conn) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(IDC_CLEAR_ALL_JOB_BARRIER);
			ps.executeUpdate();
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	private static final Map<JobKey, Job> jobMap = new HashMap<>();
	
}
