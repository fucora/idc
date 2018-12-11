package com.iwellmass.idc.executor;

import com.iwellmass.idc.model.JobEnv;
import com.iwellmass.idc.model.JobInstanceStatus;

public interface IDCJobExecutionContext {
	
	JobEnv getJobEnv();
	
	void complete(CompleteEvent event);
	
	public CompleteEvent newCompleteEvent(JobInstanceStatus status);
	
	public ProgressEvent newProgressEvent();
	
	public StartEvent newStartEvent();
	
}
