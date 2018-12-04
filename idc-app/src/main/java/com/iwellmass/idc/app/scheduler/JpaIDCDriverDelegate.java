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
import com.iwellmass.idc.app.repo.PluginVersionRepository;
import com.iwellmass.idc.model.JobBarrier;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.quartz.IDCDriverDelegate;

@Component
public class JpaIDCDriverDelegate implements IDCDriverDelegate {

	@Inject
	private JobInstanceRepository instanceRepo;
	
	@Inject
	private JobDependencyRepository dependencyRepo;
	
	@Inject
	private JobBarrierRepo barrierRepo;
	
	@Inject
	private PluginVersionRepository pluginRepo; 


	@Transactional
	public JobInstance updateJobInstance(Connection conn, Integer instanceId, Consumer<JobInstance> func)
			throws SQLException {
		JobInstance ins = instanceRepo.findOne(instanceId);
		func.accept(ins);
		return instanceRepo.save(ins);
	}

	@Override
	@Transactional
	public JobInstance selectJobInstance(Connection conn, JobKey jobKey, long shouldFireTime) throws SQLException {
		return instanceRepo.findOne(jobKey.getJobId(), jobKey.getJobGroup(), shouldFireTime);
	}

	@Override
	@Transactional
	public JobInstance selectJobInstance(Connection conn, Integer instanceId) throws SQLException {
		return instanceRepo.findOne(instanceId);
	}

	@Override
	@Transactional
	public JobInstance insertJobInstance(Connection conn, JobInstance newIns) throws SQLException {
		return instanceRepo.save(newIns);
	}

	@Override
	@Transactional
	public void batchInsertJobBarrier(Connection conn, List<JobBarrier> barriers) throws SQLException {
		barrierRepo.save(barriers);
	}

	@Override
	@Transactional
	public Integer nextInstanceId() {
		return pluginRepo.increaseInstanceSeqAndGet();
	}

	@Override
	@Transactional
	public void clearJobBarrier(Connection conn, JobKey jobKey) throws SQLException {
		// todo
	}

	@Override
	@Transactional
	public List<JobDependency> selectJobDependencies(Connection conn, JobKey jobKey) throws SQLException {
		return dependencyRepo.findDependencies(jobKey.getJobId(), jobKey.getJobGroup());
	}

	@Override
	@Transactional
	public void clearAllBarrier(Connection conn) throws SQLException {
		barrierRepo.deleteAll();
	}

	@Override
	@Transactional
	public void disableBarriers(Connection conn, String barrierId, String barrierGroup, Long shouldFireTime)
			throws SQLException {
		barrierRepo.deleteBarriers(barrierId, barrierGroup, shouldFireTime);
	}
}