package com.iwellmass.idc.app.scheduler;

import javax.inject.Inject;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.IDCLogger;
import com.iwellmass.idc.StdIDCStatusService;
import com.iwellmass.idc.dag.WorkflowService;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.quartz.IDCStore;

@Component
public class IDCPluginImpl extends IDCPlugin {
	
	@Inject
	private IDCLogger idcLogger;
	
	@Override
	public void initialize(IDCStore store) {
		
		logger = idcLogger;
		workflowService = new WorkflowService();
		
		StdIDCStatusService service = new StdIDCStatusService();
		service.setIdcLogger(idcLogger);
		service.setIdcStore(store);
	}

	@Override
	protected JobDetail buildJobDetail(Job job) {
		return JobBuilder.newJob(IDCDispatcherJob.class).build();
	}

}
