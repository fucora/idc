package com.iwellmass.idc.app.scheduler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.app.repo.JobBarrierRepo;
import com.iwellmass.idc.app.repo.JobInstanceRepository;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.quartz.IDCDriverDelegate;

public class IDCDriverDelegateImpl implements IDCDriverDelegate {
	
	@Inject
	private JobInstanceRepository instanceRepo;
	
	@Inject
	private JobBarrierRepo barrierRepo;
	

	@Transactional
	public JobInstance selectJobInstance(Connection conn, JobKey jobKey, long shouldFireTime) throws SQLException {
		return instanceRepo.findOne(jobKey.getJobId(), jobKey.getJobGroup(), shouldFireTime);
	}

	@Transactional
	public JobInstance selectJobInstance(Connection conn, Integer instanceId) throws SQLException {
		return instanceRepo.findOne(instanceId);
	}

	@Transactional
	public JobInstance insertJobInstance(Connection conn, JobInstance newIns) throws SQLException {
		// then save
		return instanceRepo.save(newIns);
	}

	@Transactional
	public void batchInsertJobBarrier(Connection conn, List<JobBarrier> barriers) throws SQLException {
		barrierRepo.save(barriers);
	}

	@Override
	public List<JobInstance> selectSubJobInstance(Connection conn, Integer mainInstanceId) throws SQLException {
		return instanceRepo.findByMainInstanceId(mainInstanceId);
	}

	@Override
	public List<JobInstance> selectRuningJobs() {
		return instanceRepo.findByStatusNotIn(Arrays.asList(JobInstanceStatus.FAILED, JobInstanceStatus.FINISHED, JobInstanceStatus.SKIPPED, JobInstanceStatus.CANCLED));
	}

	@Transactional
	@Override
	public JobInstance updateJobInstance(Connection conn, JobInstance ins) throws SQLException {
		return instanceRepo.save(ins);
	}
	

	@Transactional
	public void deleteJobBarrier(Connection conn, JobKey jobKey) throws SQLException {
		barrierRepo.deleteByJobIdAndJobGroup(jobKey.getJobId(), jobKey.getJobGroup());
	}
	
	@Transactional
	public void deleteBarrier(Connection conn, String barrierId, String barrierGroup, Long shouldFireTime)
			throws SQLException {
		barrierRepo.deleteByBarrierKey(barrierId, barrierGroup, shouldFireTime);
	}
	
	@Override
	@Transactional
	public void deleteSubJobBarrier(Connection conn, JobKey mainJobKey) {
		String jobGroup = IDCUtils.subJobGroup(mainJobKey);
		barrierRepo.deleteByJobGroup(jobGroup);
	}

	@Transactional
	public void deleteAllBarrier(Connection conn) throws SQLException {
		barrierRepo.deleteAll();
	}

	@Transactional
	public void cleanupJobInstance(Connection conn, JobKey jobKey) {
		instanceRepo.deleteByJob(jobKey);
	}
	
	@Transactional
	public void deleteJobInstance(Connection conn, JobKey jobKey) {
		instanceRepo.deleteByJob(jobKey);
	}
	
	@Transactional
	public void deleteSubJobInstance(Connection conn, Integer instanceId) {
		instanceRepo.deleteByMainInstanceId(instanceId);
	}
	
	@Transactional
	@Override
	public void deleteJobInstance(Connection conn, JobKey jobKey, long shouldFireTime) {
		instanceRepo.deleteByJobIdAndJobGroupAndShouldFireTime(jobKey.getJobId(), jobKey.getJobGroup(), shouldFireTime);
	}

}