package com.iwellmass.idc.scheduler;

import java.sql.Connection;

import org.quartz.JobPersistenceException;
import org.quartz.impl.jdbcjobstore.NoSuchDelegateException;
import org.quartz.spi.ClassLoadHelper;
import org.slf4j.Logger;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.quartz.IDCDriverDelegate;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.repo.JobRepository;

public class JpaIDCDriverDelegate extends IDCDriverDelegate {

	private JobRepository jobRepo;

	private JobInstanceRepository instanceRepo;
	
	@Override
	public void initialize(Logger logger, String tablePrefix, String schedName, String instanceId,
			ClassLoadHelper classLoadHelper, boolean useProperties, String initString) throws NoSuchDelegateException {
		super.initialize(logger, tablePrefix, schedName, instanceId, classLoadHelper, useProperties, initString);
		this.jobRepo = IDCContext.getJobRepository();
		this.instanceRepo = IDCContext.getJobInstanceRepository();
	}

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