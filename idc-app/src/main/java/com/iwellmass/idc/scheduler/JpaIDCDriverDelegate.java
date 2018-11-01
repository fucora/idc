package com.iwellmass.idc.scheduler;

import java.sql.Connection;

import javax.inject.Inject;

import org.quartz.JobPersistenceException;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.quartz.IDCDriverDelegate;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.repo.JobRepository;

@Component
public class JpaIDCDriverDelegate extends IDCDriverDelegate {

	@Inject
	private JobRepository jobRepo;

	@Inject
	private JobInstanceRepository instanceRepo;

	@Override
	public Job insertIDCJob(Connection conn, Job idcJob) throws JobPersistenceException {
		return jobRepo.save(idcJob);
	}

	@Override
	public Job getIDCJob(Connection conn, JobKey jobKey) {
		return jobRepo.findOne(jobKey);
	}

	@Override
	public JobInstance insertIDCJobInstance(Connection conn, JobInstance jobInstance) {
		return instanceRepo.save(jobInstance);
	}

	@Override
	public void updateIDCJobInstance(Connection conn, JobInstance ins) {
		instanceRepo.save(ins);
	}

	@Override
	public JobInstance getIDCJobInstance(Connection conn, Integer instanceId) {
		return instanceRepo.findOne(instanceId);
	}

}