package com.iwellmass.idc.quartz;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

public interface IDCDriverDelegate {
	
	// ~~ 实例相关 ~~
	JobInstance insertJobInstance(Connection conn, JobInstance ins) throws SQLException;
	JobInstance updateJobInstance(Connection conn, JobInstance ins) throws SQLException;
	void deleteJobInstance(Connection conn, JobKey jobKey);
	JobInstance selectJobInstance(Connection conn, Integer instanceId) throws SQLException;
	JobInstance selectJobInstance(Connection conn, JobKey jobKey, long shouldFireTime) throws SQLException;
	List<JobInstance> selectSubJobInstance(Connection conn, Integer mainInsId) throws SQLException;
	List<JobInstance> selectRuningJobs();
	void cleanupJobInstance(Connection conn, JobKey jobKey);

	// ~~ barrier 相关 ~~
	void clearAllBarrier(Connection conn) throws SQLException;
	void clearJobBarrier(Connection conn, JobKey jobKey) throws SQLException;
	void batchInsertJobBarrier(Connection conn, List<JobBarrier> barriers) throws SQLException;
	// 使 barrier 失效
	void markBarrierInvalid(Connection conn, String barrierId, String barrierGroup, Long shouldFireTime) throws SQLException;
}
