package com.iwellmass.idc.server.quartz;

import javax.inject.Inject;

import org.quartz.TriggerKey;
import org.quartz.listeners.SchedulerListenerSupport;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.model.JobStatusEvent;
import com.iwellmass.idc.server.JobStatusManager;



@Component
public class IDCSchedulerListener extends SchedulerListenerSupport{
	
	@Inject
	private JobStatusManager statusManager;

	@Override
	public void triggerPaused(TriggerKey triggerKey) {
		// 通知状态服务器
		JobStatusEvent event = new JobStatusEvent();
		statusManager.fireJobBlocked(event);
		
		/*JobKey jobKey = IDCPlugin.toJobKey(triggerKey);
		Job job = jobRepository.findOne(new JobPK(jobKey.getName(), jobKey.getGroup()));
		job.setStatus(JobStatus.PAUSED);
		jobRepository.save(job);*/
	}
}
