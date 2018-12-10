package com.iwellmass.idc.quartz;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;

public interface IDCDriverDelegate {
	
	Job selectJob(JobKey jobKey);
	Task selectTask(TaskKey taskKey);
	
	// ~~ 实例相关 ~~
	default void batchInsertJobInstance(Connection conn, List<JobInstance> newIns) throws SQLException {
		for (JobInstance ji : newIns) {
			insertJobInstance(conn, ji);
		}
	}
	JobInstance insertJobInstance(Connection conn, JobInstance newIns) throws SQLException;
	JobInstance updateJobInstance(Connection conn, Integer instanceId, Consumer<JobInstance> func) throws SQLException;
	JobInstance selectJobInstance(Connection conn, Integer instanceId) throws SQLException;
	JobInstance selectJobInstance(Connection conn, JobKey jobKey, long shouldFireTime) throws SQLException;
	List<JobInstance> selectSubJobInstance(Connection conn, Integer mainInsId) throws SQLException;

	// ~~ barrier 相关 ~~
	void clearAllBarrier(Connection conn) throws SQLException;
	void clearJobBarrier(Connection conn, JobKey jobKey) throws SQLException;
	void batchInsertJobBarrier(Connection conn, List<JobBarrier> barriers) throws SQLException;
	// 使 barrier 失效
	void markBarrierInvalid(Connection conn, String barrierId, String barrierGroup, Long shouldFireTime) throws SQLException;
}
