package com.iwellmass.idc.app.scheduler;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.JobService;
import com.iwellmass.idc.TaskService;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.quartz.IDCPlugin;

@Component
public class IDCPluginImpl extends IDCPlugin {
	
	@Inject
	private JobService jobService;
	
	@Inject
	private TaskService taskService;

	@Inject
	private DependencyService dependencyService;
	
	@Override
	protected Class<? extends org.quartz.Job> getJobClass(Task task) {
		return IDCDispatcherJob.class;
	}

	@Override
	public JobService getJobService() {
		return jobService;
	}

	@Override
	public TaskService getTaskService() {
		return taskService;
	}

	@Override
	public DependencyService getDependencyService() {
		return dependencyService;
	}
}
