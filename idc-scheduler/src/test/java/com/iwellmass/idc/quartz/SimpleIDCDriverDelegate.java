package com.iwellmass.idc.quartz;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.quartz.JobPersistenceException;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

public class SimpleIDCDriverDelegate extends IDCDriverDelegate {

	private static final Map<JobKey, Job> jobMap = new HashMap<>();
	private static final Map<Integer, JobInstance> jobInstanceMap = new HashMap<>();

	@Override
	public JobInstance insertIDCJobInstance(Connection conn, JobInstance ins) {
		synchronized (jobInstanceMap) {
			ins.setInstanceId(jobInstanceMap.size() + 1);
			jobInstanceMap.put(ins.getInstanceId(), ins);
			return ins;
		}
	}

	@Override
	public void updateIDCJobInstance(Connection conn, JobInstance ins) {
		synchronized (jobInstanceMap) {
			jobInstanceMap.put(ins.getInstanceId(), ins);
		}
	}

	@Override
	public JobInstance getIDCJobInstance(Connection conn, Integer instanceId) {
		return jobInstanceMap.get(instanceId);
	}

	@Override
	public Job insertIDCJob(Connection conn, Job idcJob) throws JobPersistenceException {
		jobMap.put(idcJob.getJobKey(), idcJob);
		return idcJob;
	}

	@Override
	public Job getIDCJob(Connection conn, JobKey jobKey) {
		return jobMap.get(jobKey);
	}
}
