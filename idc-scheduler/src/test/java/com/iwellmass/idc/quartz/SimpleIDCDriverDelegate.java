package com.iwellmass.idc.quartz;

import static org.quartz.impl.jdbcjobstore.Constants.COL_TRIGGER_GROUP;
import static org.quartz.impl.jdbcjobstore.Constants.COL_TRIGGER_NAME;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.iwellmass.idc.model.BarrierState;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

public class SimpleIDCDriverDelegate implements IDCDriverDelegate, IDCConstants {

	private static final Map<JobKey, Job> jobMap = new HashMap<>();
	private static final Map<Integer, JobInstance> jobInstanceMap = new HashMap<>();
	
	
	// ~~ SQL ~~
	public static final String IDC_INSERT_JOB = "INSERT INTO t_idc_job (job_id, job_group, task_id, group_id, content_type, task_name, description, task_type, dispatch_type, assignee, createtime, update_time, parameter, workflow_id, schedule_config, schedule_type )"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String IDC_INSERT_JOB_INSTANCE = "INSERT INTO t_idc_job_instance (instance_id, task_id, group_id, task_name, content_type, task_type, description, load_date, schedule_type, next_load_date, parameter, instance_type, assignee, start_time, end_time, status, workflow_id, should_fire_time, job_id, job_group )"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public static final String IDC_RESET_JOB_INSTANCE = "UPDATE t_idc_job_instance SET start_time = ?, end_time = ?, status = ?, parameter = ? WHERE instance_id = ?";
    
	public static final String IDC_INSERT_JOB_BARRIER = "INSERT INTO QRTZ_" + TABLE_BARRIER + "(" + COL_TRIGGER_NAME + ", "
			+ COL_TRIGGER_GROUP + ", " + COL_BARRIER_NAME + ", " + COL_BARRIER_GROUP + ", " + COL_BARRIER_SHOULD_FIRE_TIME
			+ ", " + COL_BARRIER_STATE + ") " + "VALUES (?, ?, ?, ?, ?, ?);";
	
    public static final String IDC_UPDATE_JOB_BARRIER = "DELETE FROM QRTZ_" + TABLE_BARRIER + " WHERE " + COL_BARRIER_NAME + " = ? AND "
			+ COL_BARRIER_GROUP + " = ? AND " + COL_BARRIER_SHOULD_FIRE_TIME + " = ?";
    
    public static final String IDC_CLEAR_JOB_BARRIER = "DELETE FROM QRTZ_" + TABLE_BARRIER + " WHERE " + COL_TRIGGER_NAME + " = ? AND "
    		+ COL_TRIGGER_GROUP + " = ?";
	
	
	@Override
	public Job insertJob(Connection conn, Job job) {
		jobMap.put(job.getJobKey(), job);
		return job;
	}
	
	@Override
	public Job selectJob(Connection conn, JobKey jobKey) throws SQLException {
		return jobMap.get(jobKey);
	}

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
		return jobInstanceMap.values().stream().filter(ins -> ins.getJobKey().equals(jobKey) && ins.getShouldFireTime() == shouldFireTime)
				.findFirst().get();
	}

	@Override
	public JobInstance insertJobInstance(Connection conn, JobInstance job) {
		synchronized (jobInstanceMap) {
			jobInstanceMap.put(job.getInstanceId(), job);
			return job;
		}
	}

	@Override
	public List<JobDependency> selectJobDependencies(Connection conn, JobKey idcJob) {
		return Collections.emptyList();
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
				ps.setLong(5, barrier.getShouldFireTime());
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
	public void deleteBarriers(Connection conn, String jobId, String jobGroup, Long shouldFireTime) throws SQLException {
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
	public Integer nextInstanceId() {
		return 123;
	}

	@Override
	public void clearJobBarrier(Connection conn, String jobId, String jobGroup) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(IDC_CLEAR_JOB_BARRIER);
			ps.clearParameters();
			ps.setString(1, jobId);
			ps.setString(2, jobGroup);
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
	public int countSuccessor(JobKey jobKey, long shouldFireTime) {
		return 0;
	}
}
