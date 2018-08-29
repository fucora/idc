package com.iwellmass.idc.server.quartz;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.inject.Inject;

import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.listeners.SchedulerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.ScheduleStatus;
import com.iwellmass.idc.repo.JobRepository;

public class IDCSchedulerListener extends SchedulerListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCSchedulerListener.class);

	@Inject
	private JobRepository jobRepository;
	
	@Override
	public void jobScheduled(Trigger trigger) {
		JobKey jobKey = trigger.getJobKey();

		LocalDateTime loadDate = Optional.ofNullable(trigger.getNextFireTime()).map(IDCPlugin::toLocalDateTime)
				.orElse(null);

		if (loadDate != null) {
			LOGGER.info("处理 {}.{}.{}", jobKey.getName(), jobKey.getGroup(), loadDate.format(DateTimeFormatter.BASIC_ISO_DATE));
			LOGGER.info("123");
		}

	}
	
	@Override
	public void triggerResumed(TriggerKey triggerKey) {
		JobKey key = IDCPlugin.toJobKey(triggerKey);
		Job job = getJob(key);
		job.setStatus(ScheduleStatus.NORMAL);
		jobRepository.save(job);
	}

	@Override
	public void triggerPaused(TriggerKey triggerKey) {
		JobKey key = IDCPlugin.toJobKey(triggerKey);
		Job job = getJob(key);
		job.setStatus(ScheduleStatus.PAUSED);
		jobRepository.save(job);
	}
	
	@Override
	public void triggerFinalized(Trigger trigger) {
		Job job = getJob(trigger.getJobKey());
		job.setStatus(ScheduleStatus.COMPLETE);
		jobRepository.save(job);
	}
	
	private Job getJob(JobKey jobKey) {
		String taskId = jobKey.getName();
		String groupId = jobKey.getGroup();
		return jobRepository.findOne(taskId, groupId);
	};
}
