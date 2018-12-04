package com.iwellmass.idc.quartz;

import com.iwellmass.idc.AllSimpleService;
import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.JobService;
import com.iwellmass.idc.TaskService;
import com.iwellmass.idc.model.Task;

public class SimpleIDCPlugin extends IDCPlugin {

	private AllSimpleService as;
	
	public SimpleIDCPlugin(AllSimpleService allService) {
		this.as = allService;
	}

	protected  Class<? extends org.quartz.Job> getJobClass(Task task) {
		return SimpleJob.class;
	}

	@Override
	public JobService getJobService() {
		return as;
	}

	@Override
	public TaskService getTaskService() {
		return as;
	}

	@Override
	public DependencyService getDependencyService() {
		return as;
	}

}
