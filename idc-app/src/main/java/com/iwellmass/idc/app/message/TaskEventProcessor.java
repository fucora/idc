package com.iwellmass.idc.app.message;

import java.util.*;

import com.iwellmass.idc.app.service.JobHelper;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.repository.WorkflowRepository;
import com.iwellmass.idc.scheduler.service.IDCLogger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.message.JobMessage;
import com.iwellmass.idc.scheduler.quartz.IDCJobStore;
import com.iwellmass.idc.scheduler.repository.AllJobRepository;

import lombok.Setter;

@DisallowConcurrentExecution
public class TaskEventProcessor implements org.quartz.Job {

    static final Logger LOGGER = LoggerFactory.getLogger(TaskEventProcessor.class);

    static final String CXT_JOB_SERVICE = "jobService";
    static final String CXT_JOB_STORE = "idcJobStore";
    static final String CXT_ALL_JOB_REPOSITORY = "allJobRepository";
    static final String CXT_WORKFLOW_REPOSITORY = "workflowRepository";
    static final String CXT_LOGGER = "logger";
    static final String CXT_JOB_HELPER = "jobHelper";
    static final String CXT_EXE_PARAM_HELPER = "execParamHelper";
    static final String CXT_TASK_SERVICE = "taskService";

    @Setter
    JobMessage message;

    @Setter
    IDCJobStore idcJobStore;

    @Setter
    AllJobRepository allJobRepository;

    @Setter
    WorkflowRepository workflowRepository;

    @Setter
    IDCLogger logger;

    @Setter
    JobHelper jobHelper;

    @Override
    public void execute(JobExecutionContext context) {
        // safe execute...
        try {
            LOGGER.info("接收事件，类型[{}],message = [{}]", message.getEvent().name(), message);
            doExecute(context);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + message);
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void doExecute(JobExecutionContext context) {

        if (message == null) {
            LOGGER.error("ERROR: message cannot be null, trigger {}", context.getTrigger().getKey());
            return;
        }
        Optional<AbstractJob> opt = allJobRepository.findById(message.getJobId());

        if (!opt.isPresent()) {
            // 可能是重新调度时后被删除或者误删
            LOGGER.warn("Cannot process {}, Task {} 不存在", message.getId(), message.getJobId());
            return;
        }

        AbstractJob runningJob = opt.get();
        //提前填充job
        AbstractTask abstractTask = runningJob.getTask();
        if (abstractTask.getTaskType() == TaskType.WORKFLOW) {
            Workflow workflow = workflowRepository.findById(abstractTask.getWorkflowId()).get();
            abstractTask.setWorkflow(workflow);
        }
        try {
            switch (message.getEvent()) {
                case START:
                    jobHelper.start(runningJob);
                    break;
                case FINISH:
                    jobHelper.success(runningJob);
                    break;
                case FAIL:
                    jobHelper.failed(runningJob, message);
                    break;
                case REDO:
                    jobHelper.redo(runningJob);
                    break;
                case CANCEL:
                    jobHelper.cancel(runningJob);
                    break;
                case SKIP:
                    jobHelper.skip(runningJob);
                    break;
                case READY:
                    jobHelper.ready(runningJob);
                    break;
                case RUNNING:
                    jobHelper.running(runningJob,message);
                    break;
                case TIMEOUT:
                    jobHelper.timeout(runningJob);
                    break;
                default: {
                    // bad message...
                    LOGGER.error("Cannot process {}, unsupported event {}", message.getId(), message.getEvent());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cannot process {}, {}", message.getId(), e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
    }

}
