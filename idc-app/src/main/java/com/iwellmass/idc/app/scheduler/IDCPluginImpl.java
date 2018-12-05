package com.iwellmass.idc.app.scheduler;

import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.IDCPluginService;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.quartz.IDCPlugin;

public class IDCPluginImpl extends IDCPlugin {
	
	public IDCPluginImpl(IDCPluginService pluginService, DependencyService dependencyService) {
		super(pluginService, dependencyService);
	}

	
	@Override
	protected Class<? extends org.quartz.Job> getJobClass(Task task) {
		return IDCDispatcherJob.class;
	}
}
