package com.iwellmass.idc.quartz;

import java.util.function.Consumer;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

public interface IDCStore {

	Job retrieveIDCJob(JobKey jobKey);
	
	JobInstance retrieveIDCJobInstance(JobKey jobKey, Long shouldFireTime);
	
	JobInstance retrieveIDCJobInstance(Integer instanceId);
	
	void storeIDCJobInstance(Integer instanceId, Consumer<JobInstance> act);

	void removeIDCJobBarriers(String barrierId, String barrierGroup, Long shouldFireTime);

}
