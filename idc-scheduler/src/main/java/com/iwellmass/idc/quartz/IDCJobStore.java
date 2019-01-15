package com.iwellmass.idc.quartz;

import org.quartz.JobPersistenceException;
import org.quartz.spi.JobStore;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

public interface IDCJobStore extends JobStore {

	/**
	 * 获取 JobInstance 信息
	 */
	JobInstance retrieveIDCJobInstance(Integer instanceId) throws JobPersistenceException;
	
	JobInstance cleanupIDCJobInstance(Integer instanceId) throws JobPersistenceException;
	
	// ~~ update JobInstance ~~
	JobInstance jobInstanceProgressing(ProgressEvent event) throws JobPersistenceException;
	JobInstance jobInstanceCompleted(CompleteEvent event) throws JobPersistenceException;
	
	/**
	 * <li>清理调度计划
	 * <li>清理日志信息
	 * <li>清理所有实例
	 */
	void cleanupIDCJob(JobKey jobKey) throws JobPersistenceException;

}
