package com.iwellmass.idc.app.message;

import java.util.concurrent.RejectedExecutionException;

import javax.annotation.Resource;

import com.iwellmass.idc.scheduler.repository.WorkflowRepository;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.SchedulerRepository;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.app.service.JobService;
import com.iwellmass.idc.message.JobEventService;
import com.iwellmass.idc.message.JobMessage;
import com.iwellmass.idc.scheduler.quartz.IDCJobStore;
import com.iwellmass.idc.scheduler.repository.AllJobRepository;

public class TaskEventPlugin implements SchedulerPlugin, JobEventService {

	static final Logger LOGGER = LoggerFactory.getLogger(TaskEventPlugin.class);

	public static final String NAME = TaskEventPlugin.class.getSimpleName();

	// ~~ component ~~
	public static final String PROCESSOR_JOB_NAME = "processor";
	public static final String PROCESSOR_JOB_GROUP = "message";
	public static final String PROP_MESSAGE = "message";

	private Scheduler scheduler;

	@Resource
	JobService jobService;
	
	@Resource
	IDCJobStore idcJobStore;

	@Resource
	AllJobRepository allJobRepository;

	@Resource
	WorkflowRepository workflowRepository;

	@Override
	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
		scheduler.getContext().put(NAME, this);
		scheduler.getContext().put(TaskEventProcessor.CXT_JOB_SERVICE, jobService);
		scheduler.getContext().put(TaskEventProcessor.CXT_JOB_STORE, idcJobStore);
		scheduler.getContext().put(TaskEventProcessor.CXT_ALL_JOB_REPOSITORY, allJobRepository);
		scheduler.getContext().put(TaskEventProcessor.CXT_ALL_WORKFLOW_REPOSITORY, workflowRepository);
		this.scheduler = scheduler;
	}

	@Override
	public void start() {
		try {
			// 初始化 EventProcessor
			JobDetail taskEventProcess = JobBuilder//@formatter:off
				.newJob(TaskEventProcessor.class)
				.withIdentity(PROCESSOR_JOB_NAME, PROCESSOR_JOB_GROUP)
				.requestRecovery()
				.storeDurably().build();//@formatter:on
			scheduler.addJob(taskEventProcess, true);
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		LOGGER.info("Plugin started");
	}

	@Override
	public void shutdown() {
	}

	public void send(JobMessage message) {

		LOGGER.info("接收事件 {}, message = {}", message.getId(), message);

		// TODO 判断任务堆积

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(TaskEventPlugin.PROP_MESSAGE, message);

		Trigger trigger = TriggerBuilder.newTrigger()//@formatter:off
			.withSchedule(SimpleScheduleBuilder.simpleSchedule())
			.withIdentity(message.getId())
			.forJob(TaskEventPlugin.PROCESSOR_JOB_NAME, TaskEventPlugin.PROCESSOR_JOB_GROUP)
			.usingJobData(jobDataMap)
			.build();//@formatter:on
		try {
			scheduler.scheduleJob(trigger);
		} catch (ObjectAlreadyExistsException e) {
			LOGGER.warn("Cannot replay message {} ", message.getId());
		} catch (SchedulerException e) {
			throw new RejectedExecutionException(e.getMessage(), e);
		}
	}

	public static final JobEventService eventService(String schdName) {
		try {
			Scheduler scheduler = SchedulerRepository.getInstance().lookup(schdName);
			return (JobEventService) scheduler.getContext().get(NAME);
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static final JobEventService eventService(Scheduler scheduler) {
		try {
			return (JobEventService) scheduler.getContext().get(NAME);
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
