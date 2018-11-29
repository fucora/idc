package com.iwellmass.idc.app.scheduler;

import org.springframework.stereotype.Component;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.quartz.IDCPlugin;

@Component
public class IDCPluginImpl extends IDCPlugin {
	@Override
	protected Class<? extends org.quartz.Job> getJobClass(Task task) {
		return IDCDispatcherJob.class;
	}
}
