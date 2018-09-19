package com.iwellmass.idc.executor;

import com.iwellmass.idc.model.JobInstance;

public interface IDCJobExecutionContext {
	
	JobInstance getInstance();
	
	void complete(CompleteEvent event);

	
}
