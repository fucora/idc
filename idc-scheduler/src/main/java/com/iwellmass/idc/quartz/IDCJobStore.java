package com.iwellmass.idc.quartz;

import java.util.function.Consumer;

import org.quartz.JobPersistenceException;
import org.quartz.spi.JobStore;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

public interface IDCJobStore extends JobStore {

	Job retrieveIDCJob(JobKey jobKey) throws JobPersistenceException;
	
	JobInstance retrieveIDCJobInstance(Integer instanceId) throws JobPersistenceException;
	
	JobInstance retrieveIDCJobInstance(JobKey jobKey, Long shouldFireTime) throws JobPersistenceException;
	
	void storeIDCJobInstance(Integer instanceId, Consumer<JobInstance> act) throws JobPersistenceException;

	// void removeIDCJobBarriers(String barrierId, String barrierGroup, Long shouldFireTime);

	void completeIDCJobInstance(CompleteEvent event) throws JobPersistenceException;

}
