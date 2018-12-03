package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.*;
import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.model.JobEnv;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.WorkflowEdge;

@DisallowConcurrentExecution
public class IDCWorkflowJob implements org.quartz.Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		IDCPlugin plugin = IDC_PLUGIN.applyGet(context.getScheduler());
		
		// Task 实例
		Task task = IDCUtils.getObject(context.getJobDetail().getJobDataMap(), Task.class).apply(TASK_JSON);
		
		// 任务实例
		JobInstance jobInstance = CONTEXT_INSTANCE.applyGet(context);
		
		// next tasks
		List<TaskKey> successors = plugin.getDependencyService().getSuccessors(task.getWorkflowId(), WorkflowEdge.START);
		Assert.isFalse(Utils.isNullOrEmpty(successors), "获取工作流子任务失败");
		
		List<Task> subTasks = plugin.getTaskService().getTasks(successors);
		for (Task subTask : subTasks) {
			try {
				// 构建 runtime 信息
				plugin.scheduleSubTask(subTask, jobInstance.getInstanceId());
			} catch (SchedulerException e) {
				throw new JobExecutionException("执行工作流子任务失败: " + e.getMessage(), e);
			}
		}
	}
}
