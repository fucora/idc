package com.iwellmass.idc.quartz;

import java.util.List;
import java.util.function.Consumer;

import org.quartz.JobPersistenceException;
import org.quartz.spi.JobStore;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

public interface IDCJobStore extends JobStore {

	JobInstance retrieveIDCJobInstance(Integer instanceId) throws JobPersistenceException;
	
	JobInstance completeIDCJobInstance(CompleteEvent event) throws JobPersistenceException;
	
	void storeIDCJobInstance(Integer instanceId, Consumer<JobInstance> act) throws JobPersistenceException;

	void clearAllBarrier();

	List<JobInstance> retrieveIDCSubJobInstance(Integer mainInstanceId) throws JobPersistenceException;

	void cleanupIDCJob(JobKey jobKey) throws JobPersistenceException;
}
