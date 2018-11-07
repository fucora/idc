package com.iwellmass.idc.scheduler;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.iwellmass.idc.dag.Workflow;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.quartz.IDCPlugin;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class IDCWorkflowJob implements org.quartz.Job {
	
	private int workflowId;

	private Workflow workflow;
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		IDCPlugin plugin = IDC_PLUGIN.applyGet(context.getScheduler());

		JobKey jobKey = new JobKey();

	}

}
