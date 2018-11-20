package com.iwellmass.idc.quartz;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

public interface IDCDriverDelegate {

	Integer nextInstanceId();
	
	Job insertJob(Connection conn, Job job) throws SQLException;

	Job selectJob(Connection conn, JobKey jobKey) throws SQLException;
	
	JobInstance updateJobInstance(Connection conn, Integer instanceId, Consumer<JobInstance> func) throws SQLException;

	JobInstance selectJobInstance(Connection conn, JobKey jobKey, long shouldFireTime) throws SQLException;

	JobInstance selectJobInstance(Connection conn, Integer instanceId) throws SQLException;
	
	JobInstance insertJobInstance(Connection conn, JobInstance newIns) throws SQLException;
	
	List<JobDependency> selectJobDependencies(Connection conn, JobKey idcJob) throws SQLException;
	
	void batchInsertJobBarrier(Connection conn, List<JobBarrier> barriers) throws SQLException;

	void deleteBarriers(Connection conn, String barrierId, String barrierGroup, Long shouldFireTime) throws SQLException;

	void clearJobBarrier(Connection conn, String jobId, String jobGroup) throws SQLException;

	int countSuccessor(JobKey jobKey, long shouldFireTime);
	
	
}
