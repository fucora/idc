package com.iwellmass.idc.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;

import com.iwellmass.idc.model.Task;

public class SimpleIDCPlugin extends IDCPlugin {
	
	@Override
	protected JobDetail buildJobDetail(Task task) {
		
		System.out.println(123);
		return JobBuilder
				.newJob(SimpleJob.class)
				.withIdentity(task.getTaskId(), task.getTaskGroup())
				.requestRecovery()
				.storeDurably().build();
	}

}
