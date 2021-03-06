package com.iwellmass.idc.app.message;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.RejectedExecutionException;

import javax.annotation.Resource;
import javax.inject.Inject;

import com.iwellmass.idc.app.service.ExecParamHelper;
import com.iwellmass.idc.app.service.JobHelper;
import com.iwellmass.idc.app.service.TaskService;
import com.iwellmass.idc.message.TimeoutMessage;
import com.iwellmass.idc.scheduler.repository.WorkflowRepository;
import com.iwellmass.idc.scheduler.service.IDCLogger;
import lombok.Setter;
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
import org.springframework.beans.factory.annotation.Value;

public class TaskEventPlugin implements SchedulerPlugin, JobEventService {

    static final Logger LOGGER = LoggerFactory.getLogger(TaskEventPlugin.class);

    public static final String NAME = TaskEventPlugin.class.getSimpleName();

    // ~~ component ~~
    public static final String PROCESSOR_JOB_NAME = "processor";
    public static final String PROCESSOR_JOB_GROUP = "message";
    public static final String PROP_MESSAGE = "message";

    private Scheduler scheduler;

    @Setter
    @Resource
    JobService jobService;

    @Setter
    @Resource
    IDCJobStore idcJobStore;

    @Setter
    @Resource
    AllJobRepository allJobRepository;

    @Setter
    @Resource
    WorkflowRepository workflowRepository;

    @Setter
    @Inject
    IDCLogger logger;

    @Setter
    @Resource
    JobHelper jobHelper;

    @Setter
    @Resource
    ExecParamHelper execParamHelper;

    @Setter
    @Resource
    TaskService taskService;

    @Value(value = "${idc.scheduler.callbackTimeout:1800}")
    Long timeout;

    @Override
    public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
        jobHelper.setScheduler(scheduler);
        taskService.setScheduler(scheduler);
        scheduler.getContext().put(NAME, this);
        scheduler.getContext().put(TaskEventProcessor.CXT_JOB_SERVICE, jobService);
        scheduler.getContext().put(TaskEventProcessor.CXT_JOB_STORE, idcJobStore);
        scheduler.getContext().put(TaskEventProcessor.CXT_ALL_JOB_REPOSITORY, allJobRepository);
        scheduler.getContext().put(TaskEventProcessor.CXT_WORKFLOW_REPOSITORY, workflowRepository);
        scheduler.getContext().put(TaskEventProcessor.CXT_LOGGER, logger);
        scheduler.getContext().put(TaskEventProcessor.CXT_JOB_HELPER, jobHelper);
        scheduler.getContext().put(TaskEventProcessor.CXT_EXE_PARAM_HELPER, execParamHelper);
        scheduler.getContext().put(TaskEventProcessor.CXT_TASK_SERVICE, taskService);
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

        // TODO 判断任务堆积
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(TaskEventPlugin.PROP_MESSAGE, message);
        Trigger trigger = TriggerBuilder.newTrigger()//@formatter:off
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .startAt(message instanceof TimeoutMessage ? new Date(LocalDateTime.now().plusSeconds(timeout).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) : new Date())
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
