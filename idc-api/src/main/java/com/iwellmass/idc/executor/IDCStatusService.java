package com.iwellmass.idc.executor;

public interface IDCStatusService {
	
	void fireStartEvent(StartEvent event);
	
	void fireCompleteEvent(CompleteEvent event);

	void fireProgressEvent(ProgressEvent setMessage);
	
}
