package com.iwellmass.idc.quartz;

import java.util.function.Consumer;

import org.quartz.JobPersistenceException;
import org.quartz.spi.JobStore;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.JobInstance;

public interface IDCJobStore extends JobStore {

	JobInstance retrieveIDCJobInstance(Integer instanceId) throws JobPersistenceException;
	
	void storeIDCJobInstance(Integer instanceId, Consumer<JobInstance> act) throws JobPersistenceException;

	JobInstance completeIDCJobInstance(CompleteEvent event) throws JobPersistenceException;

	void clearAllBarrier();

}
