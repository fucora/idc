package com.iwellmass.idc.executor;

public interface IDCJobExecutionContext {
	
	public Integer getInstanceId();
	
	public IDCJob getIDCJob();
	
	public void complete(CompleteEvent event);
	
}
