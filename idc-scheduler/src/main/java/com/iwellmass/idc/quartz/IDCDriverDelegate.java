package com.iwellmass.idc.quartz;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

public interface IDCDriverDelegate {

	Integer nextInstanceId();

	// ~~ 实例相关 ~~
	JobInstance insertJobInstance(Connection conn, JobInstance newIns) throws SQLException;
	JobInstance updateJobInstance(Connection conn, Integer instanceId, Consumer<JobInstance> func) throws SQLException;
	JobInstance selectJobInstance(Connection conn, Integer instanceId) throws SQLException;
	JobInstance selectJobInstance(Connection conn, JobKey jobKey, long shouldFireTime) throws SQLException;
	
	// ~~ barrier 相关 ~~
	void clearAllBarrier(Connection conn) throws SQLException;
	void clearJobBarrier(Connection conn, JobKey jobKey) throws SQLException;
	void batchInsertJobBarrier(Connection conn, List<JobBarrier> barriers) throws SQLException;
	void disableBarriers(Connection conn, String barrierId, String barrierGroup, Long shouldFireTime) throws SQLException;
}
