package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import java.time.LocalDateTime;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSON;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.GuardEnv;
import com.iwellmass.idc.model.JobInstanceStatus;

@DisallowConcurrentExecution
public class IDCWorkflowGuardJob implements org.quartz.Job {
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		IDCPlugin plugin = IDC_PLUGIN.applyGet(context.getScheduler());
		
		GuardEnv redoEnv = JSON.parseObject(IDCContextKey.JOB_RUNTIME.applyGet(context.getTrigger().getJobDataMap()), GuardEnv.class);
		
		plugin.getStatusService().fireCompleteEvent(CompleteEvent.successEvent()
			.setMessage("所有子任务执行完毕")
			.setEndTime(LocalDateTime.now()).setFinalStatus(JobInstanceStatus.FINISHED)
			.setInstanceId(redoEnv.getInstanceId()));
	}
}
