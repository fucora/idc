package com.iwellmass.idc.server.quartz;

import static com.iwellmass.idc.server.quartz.IDCConstants.CONTEXT_INSTANCE_ID;
import static com.iwellmass.idc.server.quartz.IDCConstants.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.server.quartz.IDCPlugin.buildInstanceId;
import static com.iwellmass.idc.server.quartz.IDCPlugin.getTriggerType;
import static com.iwellmass.idc.server.quartz.IDCPlugin.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.Date;

import javax.inject.Inject;

import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.Sentinel;
import com.iwellmass.idc.model.SentinelStatus;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.repo.JobRepository;
import com.iwellmass.idc.repo.SentinelRepository;

public class IDCTriggerListener extends TriggerListenerSupport implements ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCTriggerListener.class);

	ApplicationContext applicationContext;

	@Inject
	private JobRepository jobRepository;

	@Inject
	private JobInstanceRepository jobInstanceRepository;

	@Inject
	private SentinelRepository sentinelRepository;

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {

		JobKey jobKey = trigger.getJobKey();
		String taskId = jobKey.getName();
		String groupId = jobKey.getGroup();
		LocalDateTime loadDate = toLocalDateTime(context.getScheduledFireTime());
		
		// 生成业务哨兵
		Date nextFireTime = context.getNextFireTime();
		if (nextFireTime != null) {
			Sentinel sentinel = new Sentinel();
			sentinel.setTaskId(jobKey.getName());
			sentinel.setGroupId(jobKey.getGroup());
			sentinel.setLoadDate(nextFireTime.getTime());
			sentinel.setStatus(SentinelStatus.WAITING);
			sentinelRepository.save(sentinel);
			LOGGER.info("job instance {}'s sentinel created", buildInstanceId(taskId, groupId, toLocalDateTime(nextFireTime)));
		}

		// 生成实例
		Job job = jobRepository.findOne(taskId, groupId);
		String instanceId = buildInstanceId(taskId, groupId, loadDate);
		JobInstance jobInstance = new JobInstance();
		jobInstance.setId(instanceId);
		jobInstance.setTaskId(taskId);
		jobInstance.setGroupId(groupId);
		jobInstance.setLoadDate(loadDate);
		jobInstance.setType(getTriggerType(trigger));
		jobInstance.setAssignee(job.getAssignee());
		jobInstance.setTaskType(job.getTaskType());
		jobInstance.setStatus(JobInstanceStatus.NEW);
		// TODO 参数信息
		jobInstance.setParameters(null); 
		jobInstance.setStartTime(null);
		jobInstance.setEndTime(null);
		jobInstanceRepository.save(jobInstance);
		LOGGER.info("job instance {} up-to-date", instanceId);
		
		// 初始化执行环境
		CONTEXT_LOAD_DATE.applyPut(context, loadDate);
		CONTEXT_INSTANCE_ID.applyPut(context, instanceId);
	}
	
	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		return IDCConstants.JOB_EXECUTION_SKIP.applyGet(context);
	}
	

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@Override
	public String getName() {
		return IDCTriggerListener.class.getName();
	}
}
