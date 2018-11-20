package com.iwellmass.idc.app.scheduler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.app.repo.JobBarrierRepo;
import com.iwellmass.idc.app.repo.JobDependencyRepository;
import com.iwellmass.idc.app.repo.JobInstanceRepository;
import com.iwellmass.idc.app.repo.JobRepository;
import com.iwellmass.idc.app.repo.PluginVersionRepository;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.quartz.IDCDriverDelegate;

@Component
public class JpaIDCDriverDelegate implements IDCDriverDelegate {

	@Inject
	private JobRepository jobRepo;

	@Inject
	private JobInstanceRepository instanceRepo;
	
	@Inject
	private JobDependencyRepository dependencyRepo;
	
	@Inject
	private JobBarrierRepo barrierRepo;
	
	@Inject
	private PluginVersionRepository pluginRepo; 

	@Override
	public Job insertJob(Connection conn, Job job) throws SQLException {
		return jobRepo.save(job);
	}

	@Override
	public Job selectJob(Connection conn, JobKey jobKey) throws SQLException {
		return jobRepo.findOne(jobKey);
	}

	@Transactional
	public JobInstance updateJobInstance(Connection conn, Integer instanceId, Consumer<JobInstance> func)
			throws SQLException {
		JobInstance ins = instanceRepo.findOne(instanceId);
		func.accept(ins);
		return instanceRepo.save(ins);
	}

	@Override
	public JobInstance selectJobInstance(Connection conn, JobKey jobKey, long shouldFireTime) throws SQLException {
		return instanceRepo.findOne(jobKey.getJobId(), jobKey.getJobGroup(), shouldFireTime);
	}

	@Override
	public JobInstance selectJobInstance(Connection conn, Integer instanceId) throws SQLException {
		return instanceRepo.findOne(instanceId);
	}

	@Override
	public JobInstance insertJobInstance(Connection conn, JobInstance newIns) throws SQLException {
		return instanceRepo.save(newIns);
	}

	@Override
	public List<Job> selectJobDependencies(Connection conn, Job idcJob) throws SQLException {
		return dependencyRepo.findDependencies(idcJob.getTaskId(), idcJob.getGroupId());
	}

	@Override
	public void batchInsertJobBarrier(Connection conn, List<JobBarrier> barriers) throws SQLException {
		barrierRepo.save(barriers);
	}

	@Override
	public void deleteBarriers(Connection conn, String barrierId, String barrierGroup, Long shouldFireTime)
			throws SQLException {
		barrierRepo.deleteBarriers(barrierId, barrierGroup, shouldFireTime);
	}

	@Override
	public void clearJobBarrier(Connection conn, String jobId, String jobGroup) throws SQLException {
		barrierRepo.clearJobBarrier(jobId, jobGroup);
	}

	@Override
	public Integer nextInstanceId() {
		return pluginRepo.increaseInstanceSeqAndGet();
	}

}