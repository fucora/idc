package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.dag.Workflow;
import com.iwellmass.idc.dag.WorkflowService;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.TaskKey;

@DisallowConcurrentExecution
public class IDCWorkflowJob implements org.quartz.Job {

	private WorkflowService workflowManager;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		TaskKey taskKey = IDCUtils.getTaskKey(context.getTrigger());
		Workflow workflow = workflowManager.getWorkflow(taskKey);
		
		Job job = workflow.getMainJob();
		
		List<Job> subJobs = workflow.getAllSubJob();
		
		if (subJobs == null || subJobs.isEmpty()) {
			throw new JobExecutionException("获取工作流子任务失败");
		}
		
		List<Job> firstJobs = workflow.computeFirstJob();

		if (firstJobs == null || firstJobs.isEmpty()) {
			throw new JobExecutionException("获取工作流子任务失败");
		}
		
		IDCPlugin idcPlugin = IDC_PLUGIN.applyGet(context.getScheduler());

		for (Job subJob : firstJobs) {
			try {
				idcPlugin.scheduleSubJob(job, subJob);
			} catch (SchedulerException e) {
				throw new JobExecutionException("执行工作流子任务失败: " + e.getMessage(), e);
			}
		}
	}

}
