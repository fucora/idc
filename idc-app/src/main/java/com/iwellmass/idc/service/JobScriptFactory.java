package com.iwellmass.idc.service;

import javax.inject.Inject;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobScript;
import com.iwellmass.idc.scheduler.IDCDispatcherJob;

@Component
public class JobScriptFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobScriptFactory.class);
	
	@Inject
	private Scheduler scheduler;


	public JobScript getJobScript(Job job) {
		try {
			// 首先生成 JobDetails
			JobDetail jobDetail = JobBuilder
				.newJob(IDCDispatcherJob.class)
				.withIdentity(new JobKey(job.getTaskId(), job.getGroupId()))
				.requestRecovery().storeDurably()
				.build();

			scheduler.addJob(jobDetail, true);
			return new JobScript() {
				@Override
				public String getScriptId() {
					return jobDetail.getKey().getName();
				}
				@Override
				public String getScriptGroup() {
					return jobDetail.getKey().getGroup();
				}
			};
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AppException("获取 JobScript 时出错: " + e.getMessage());
		}
	}

}
