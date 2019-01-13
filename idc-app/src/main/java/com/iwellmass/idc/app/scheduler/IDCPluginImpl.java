package com.iwellmass.idc.app.scheduler;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.quartz.IDCPluginService;

public class IDCPluginImpl extends IDCPlugin {
	
	public IDCPluginImpl(IDCPluginService pluginService) {
		super(pluginService);
	}

	
	@Override
	protected Class<? extends org.quartz.Job> getJobClass(Task task) {
		return IDCDispatcherJob.class;
	}
}
