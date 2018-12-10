package com.iwellmass.idc.executor;

import com.iwellmass.idc.model.JobEnv;

public interface IDCJobExecutionContext {
	
	JobEnv getJobEnv();
	
	void complete(CompleteEvent event);

}
