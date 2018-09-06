package com.iwellmass.idc.executor;

public interface IDCJob {
	
	public void execute(IDCJobExecutionContext context);
	
	public default String getContentType() {
		return this.getClass().getSimpleName();
	}
}

