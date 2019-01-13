package com.iwellmass.idc.app.scheduler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

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

	public JobInstance insertJobInstance(Connection conn, JobInstance newIns) throws SQLException {
		// clean first
		instanceRepo.deleteByJobIdAndJobGroupAndShouldFireTime(newIns.getJobId(), newIns.getJobGroup(), newIns.getShouldFireTime());
		// then save
		return instanceRepo.save(newIns);
	}

	@Transactional
	public void batchInsertJobBarrier(Connection conn, List<JobBarrier> barriers) throws SQLException {
		barrierRepo.save(barriers);
	}

	@Transactional
	public void clearJobBarrier(Connection conn, JobKey jobKey) throws SQLException {
		barrierRepo.clearJobBarrier(jobKey.getJobId(), jobKey.getJobGroup());
	}

	@Transactional
	public void clearAllBarrier(Connection conn) throws SQLException {
		barrierRepo.deleteAll();
	}

	@Transactional
	public void markBarrierInvalid(Connection conn, String barrierId, String barrierGroup, Long shouldFireTime)
			throws SQLException {
		barrierRepo.deleteBarriers(barrierId, barrierGroup, shouldFireTime);
	}


	@Override
	public List<JobInstance> selectSubJobInstance(Connection conn, Integer mainInstanceId) throws SQLException {
		return instanceRepo.findByMainInstanceId(mainInstanceId);
	}

	@Transactional
	public void cleanupJobInstance(Connection conn, JobKey jobKey) {
		instanceRepo.deleteByJob(jobKey);
	}

	@Override
	public List<JobInstance> selectRuningJobs() {
		return instanceRepo.findByStatusNotIn(Arrays.asList(JobInstanceStatus.FAILED, JobInstanceStatus.FINISHED, JobInstanceStatus.SKIPPED, JobInstanceStatus.CANCLED));
	}

	@Override
	public void deleteJobInstance(Connection conn, JobKey jobKey) {
		instanceRepo.deleteByJob(jobKey);
	}

	@Override
	public JobInstance updateJobInstance(Connection conn, JobInstance ins) throws SQLException {
		return instanceRepo.save(ins);
	}
}