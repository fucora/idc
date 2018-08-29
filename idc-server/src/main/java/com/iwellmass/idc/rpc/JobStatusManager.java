package com.iwellmass.idc.rpc;

import com.iwellmass.idc.model.CompleteEvent;

public interface JobStatusManager {
	
	void fireJobComplete(CompleteEvent event);


}
