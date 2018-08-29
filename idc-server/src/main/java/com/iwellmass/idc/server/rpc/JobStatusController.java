package com.iwellmass.idc.server.rpc;

import org.springframework.stereotype.Controller;

import com.iwellmass.idc.model.CompleteEvent;

@Controller
public class JobStatusController {
	
	private JobStatusManager jobStatusManager;
	
	public void fireJobComplete(CompleteEvent event) {
		jobStatusManager.fireJobComplete(event);
	}
	
	
}
