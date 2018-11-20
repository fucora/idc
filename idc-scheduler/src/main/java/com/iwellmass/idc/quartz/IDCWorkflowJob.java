package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_JOB;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.iwellmass.idc.dag.WorkflowService;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleEnv;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;

@DisallowConcurrentExecution
public class IDCWorkflowJob implements org.quartz.Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		IDCPlugin plugin = IDC_PLUGIN.applyGet(context.getScheduler());
		WorkflowService workflowService = plugin.getWorkflowService();
		
		// 取出任务实例
		Job job = CONTEXT_JOB.applyGet(context);
		JobInstance jobInstance = CONTEXT_INSTANCE.applyGet(context); // TASK + TASK
		
		List<Task> successors = workflowService.successors(jobInstance.getWorkflowId(), WorkflowService.START);
		
		if (successors == null || successors.isEmpty()) {
			throw new JobExecutionException("获取工作流子任务失败");
		}
		
		for (Task subTask : successors) {
			try {
				ScheduleProperties schdProps = job.getScheduleProperties();
				ScheduleEnv scheduleRuntime = new ScheduleEnv();
				scheduleRuntime.setParameter(jobInstance.getParameter());
				scheduleRuntime.setShouldFireTime(jobInstance.getShouldFireTime());
				plugin.schedule(subTask, schdProps);
			} catch (SchedulerException e) {
				throw new JobExecutionException("执行工作流子任务失败: " + e.getMessage(), e);
			}
		}
	}
}
