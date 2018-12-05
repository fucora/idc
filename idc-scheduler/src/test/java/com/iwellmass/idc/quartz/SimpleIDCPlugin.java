package com.iwellmass.idc.quartz;

import com.iwellmass.idc.AllSimpleService;
import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.IDCPluginService;
import com.iwellmass.idc.model.Task;

public class SimpleIDCPlugin extends IDCPlugin {


	public SimpleIDCPlugin(IDCPluginService pluginRepository, DependencyService dependencyService) {
		super(pluginRepository, dependencyService);
	}

	private AllSimpleService as;
	

	protected  Class<? extends org.quartz.Job> getJobClass(Task task) {
		return SimpleJob.class;
	}

	@Override
	public DependencyService getDependencyService() {
		return as;
	}

}
