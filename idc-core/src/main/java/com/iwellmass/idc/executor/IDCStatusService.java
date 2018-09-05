package com.iwellmass.idc.executor;

public interface IDCStatusService {
	
	public void fireStartEvent(StartEvent event);
	
	public void fireCompleteEvent(CompleteEvent event);
	
}
