package com.iwellmass.idc.quartz;

import com.iwellmass.idc.model.Task;

public class SimpleIDCPlugin extends IDCPlugin {


	public SimpleIDCPlugin(IDCPluginService pluginRepository) {
		super(pluginRepository);
	}

	private AllSimpleService as;
	

	protected  Class<? extends org.quartz.Job> getJobClass(Task task) {
		return SimpleJob.class;
	}


}
