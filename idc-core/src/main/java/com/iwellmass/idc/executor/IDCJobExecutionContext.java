package com.iwellmass.idc.executor;

import com.iwellmass.idc.model.JobInstance;

public interface IDCJobExecutionContext {
	
	public JobInstance getInstance();
	
	public void complete(CompleteEvent event);

}
