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
	void deleteJobInstance(Connection conn, JobKey jobKey, long shouldFireTime);
	void deleteSubJobInstance(Connection conn, Integer instanceId);
	JobInstance selectJobInstance(Connection conn, Integer instanceId) throws SQLException;
	JobInstance selectJobInstance(Connection conn, JobKey jobKey, long shouldFireTime) throws SQLException;
	List<JobInstance> selectSubJobInstance(Connection conn, Integer mainInsId) throws SQLException;
	List<JobInstance> selectRuningJobs();

	// ~~ barrier 相关 ~~
	void batchInsertJobBarrier(Connection conn, List<JobBarrier> barriers) throws SQLException;
	void deleteAllBarrier(Connection conn) throws SQLException;
	void deleteJobBarrier(Connection conn, JobKey jobKey) throws SQLException;
	void deleteSubJobBarrier(Connection conn, JobKey jobKey) throws SQLException;
	// 使 barrier 失效
	void deleteBarrier(Connection conn, String barrierId, String barrierGroup, Long shouldFireTime) throws SQLException;
	
}
