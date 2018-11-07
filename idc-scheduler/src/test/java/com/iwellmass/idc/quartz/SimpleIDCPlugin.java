package com.iwellmass.idc.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;

import com.iwellmass.idc.StdIDCStatusService;
import com.iwellmass.idc.dag.WorkflowService;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.model.Job;

public class SimpleIDCPlugin extends IDCPlugin {


	protected JobDetail buildJobDetail(Job job) {
		return JobBuilder
				.newJob(SimpleJob.class)
				.withIdentity(job.getTaskId(), job.getGroupId())
				.requestRecovery()
				.storeDurably().build();
	}

	@Override
	protected IDCStatusService initIDCStatusService() {
		StdIDCStatusService service = new StdIDCStatusService();
		service.setIdcLogger(logger);
		service.setIdcStore(store);
		return service;
	}

	@Override
	protected WorkflowService initWorkflowService() {
		return new WorkflowService();
	}

}
