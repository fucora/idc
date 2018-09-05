package com.iwellmass.idc.executor;

import com.iwellmass.idc.model.JobInstance;

public interface IDCJobExecutorService {
	
	String RESOURCE_URI_TEMPLATE = "/idc-job/{instanceId}/execution";
	
	public void execute(JobInstance context);
	
}
