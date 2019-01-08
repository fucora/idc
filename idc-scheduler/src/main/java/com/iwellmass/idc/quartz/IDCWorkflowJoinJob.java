package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.alibaba.fastjson.JSON;
import com.iwellmass.idc.model.JoinEnv;
import com.iwellmass.idc.model.TaskKey;

@DisallowConcurrentExecution
public class IDCWorkflowJoinJob implements org.quartz.Job {
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		IDCPlugin plugin = IDC_PLUGIN.applyGet(context.getScheduler());
		
		// joinEnv
		JoinEnv joinEnv = JSON.parseObject(IDCContextKey.JOB_RUNTIME.applyGet(context.getTrigger().getJobDataMap()), JoinEnv.class);
		
		try {
			List<TaskKey> successor = plugin.getDependencyService().getSuccessors(joinEnv.getMainTaskKey(), joinEnv.getJoinKey());
			if (successor.isEmpty()) {
				throw new JobExecutionException("未找到后续任务");
			}
			for (TaskKey tk : successor) {
				plugin.scheduleSubTask(tk, joinEnv.getInstanceId());
			}
		} catch (SchedulerException e) {
			throw new JobExecutionException(e.getMessage(), e);
		}
	}
}
