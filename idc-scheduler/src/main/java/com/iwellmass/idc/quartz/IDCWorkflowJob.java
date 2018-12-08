package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.WorkflowEdge;

@DisallowConcurrentExecution
public class IDCWorkflowJob implements org.quartz.Job {
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		IDCPlugin plugin = IDC_PLUGIN.applyGet(context.getScheduler());
		
		// 任务实例
		JobInstance jobInstance = CONTEXT_INSTANCE.applyGet(context);
		
		List<TaskKey> success = plugin.scheduleSucessor(WorkflowEdge.START, jobInstance);
		
		if (success.isEmpty()) {
			throw new JobExecutionException("未找到子任务");
		}
		
	}
}
