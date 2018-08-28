package com.iwellmass.idc.server;

import com.iwellmass.idc.model.CompleteEvent;
import com.iwellmass.idc.model.JobStatusEvent;
import com.iwellmass.idc.model.StartEvent;

public interface JobStatusManager {
	
	void fireJobActived(JobStatusEvent event);
	
	void fireJobStart(StartEvent event);

	void fireJobComplete(CompleteEvent event);

	void fireJobBlocked(JobStatusEvent event);

}
