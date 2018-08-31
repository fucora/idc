package com.iwellmass.idc.controller;

import org.springframework.stereotype.Controller;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.service.JobStatusManager;

@Controller
public class JobStatusController {

	private JobStatusManager jobStatusManager;

	public void fireJobComplete(CompleteEvent event) {
	}

}
