package com.iwellmass.idc.app.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.message.FailMessage;
import com.iwellmass.idc.message.FinishMessage;
import com.iwellmass.idc.message.JobMessage;
import com.iwellmass.idc.message.TimeoutMessage;
import com.iwellmass.idc.scheduler.IDCJobExecutors;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.quartz.IDCJobStore;
import com.iwellmass.idc.scheduler.quartz.IDCJobstoreCMT;
import com.iwellmass.idc.scheduler.quartz.ReleaseInstruction;
import com.iwellmass.idc.scheduler.repository.AllJobRepository;
import com.iwellmass.idc.scheduler.repository.JobRepository;
import com.iwellmass.idc.scheduler.repository.NodeJobRepository;
import com.iwellmass.idc.scheduler.repository.WorkflowRepository;
import com.iwellmass.idc.scheduler.service.IDCLogger;
import lombok.Setter;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author nobita chen
 * @email nobita0522@qq.com
 * @date 2019/6/25 15:04
 */
@Component
public class JobHelper {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);

    @Setter
    private Scheduler scheduler;

    @Inject
    private IDCLogger logger;
    @Inject
    IDCJobStore idcJobStore;
    @Inject
    AllJobRepository allJobRepository;
    @Inject
    WorkflowRepository workflowRepository;
    @Inject
    JobRepository jobRepository;
    @Inject
    NodeJobRepository nodeJobRepository;
    @Inject
    IDCJobstoreCMT idcJobstoreCMT;
    @Inject
    JobService jobService;
    @Inject
    ExecParamHelper execParamHelper;
    @Value(value = "${idc.scheduler.openCallbackControl:false}")
    boolean openCallbackControl;

    //==============================================  processor call

    public void start(AbstractJob job) {
        if (job.getState().isComplete()) {
            throw new JobException("任务已执行");
        }
        if (job.getTask() == null) {
            throw new JobException("任务不存在");
        }
        if (job.getTaskType() == TaskType.WORKFLOW) {
            executeJob(job.asJob());
        } else {
            executeNodeJob(job.asNodeJob());
        }
    }

    public void success(AbstractJob abstractJob) {
//        checkRunning(abstractJob);
        modifyJobState(abstractJob, JobState.FINISHED);
        onJobFinished(abstractJob);
    }

    public void failed(AbstractJob abstractJob, JobMessage message) throws JsonProcessingException {
        checkRunning(abstractJob);
        modifyJobState(abstractJob, JobState.FAILED);
        TriggerKey triggerKey;
        if (abstractJob.isJob()) {
            triggerKey = abstractJob.asJob().getTask().getTriggerKey();
            ExecutionLog executionLog = ExecutionLog.createLog(abstractJob.getId(), "任务实例执行失败，批次时间[{}]，taskName[{}]，jobId[{}]，loadDate[{}]，workflowId[{}]",
                    null,
                    abstractJob.asJob().getShouldFireTime().format(formatter), abstractJob.asJob().getTask().getTaskName(), abstractJob.getId(), abstractJob.asJob().getLoadDate(), abstractJob.asJob().getTask().getWorkflowId());
            logger.log(executionLog);
        } else {
            // modify node'parent   state to failed
            ExecutionLog nodeJobExecutionLog = ExecutionLog.createLog(abstractJob.getId(), "节点任务执行失败，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                    message.getStackTraceElements() == null ? null : new ObjectMapper().writeValueAsString(message.getStackTraceElements()),
                    abstractJob.asNodeJob().getNodeTask().getTaskId(), abstractJob.asNodeJob().getNodeTask().getDomain(), abstractJob.getId(), abstractJob.getState().name());
            logger.log(nodeJobExecutionLog);
            Job parent = jobRepository.findById(abstractJob.asNodeJob().getContainer()).get();
            triggerKey = parent.getTask().getTriggerKey();
            modifyJobState(parent, JobState.FAILED);
            ExecutionLog jobExecutionLog = ExecutionLog.createLog(parent.getId(), "任务实例执行失败，批次时间[{}]，taskName[{}]，jobId[{}]，loadDate[{}]，workflowId[{}]",
                    null,
                    parent.getShouldFireTime().format(formatter), parent.getTask().getTaskName(), parent.getId(), parent.getLoadDate(), parent.getTask().getWorkflowId());
            logger.log(jobExecutionLog);
        }
        try {
            if (scheduler.checkExists(triggerKey)) {
                idcJobStore.releaseTrigger(triggerKey, ReleaseInstruction.SET_ERROR);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

    /**
     * redo job:adapt different strategy with the kind of job ,the kind of absJob contain nodeJob and job
     * when job is nodeJob: clear the nodeJob and generate a new nodeJob instance,the new nodeJob instance contain new jobId,but the containnerId must use oldJod's containerId
     * when job is task's instance,job : the jobId can't be generated then clear all subJobs of the job and recreate all job's subJobs
     *
     * @param job
     */
    public void redo(AbstractJob job) {
        if (job.getState().isSuccess()) {
            throw new AppException("该job已完成:" + job.getId());
        }
        if (job.getTaskType() == TaskType.WORKFLOW) {
            // cache running param
            List<ExecParam> execParams = job.asJob().getParams();
            logger.log(job.getId(), "重跑任务实例，批次时间[{}]，taskName[{}]，jobId[{}]，loadDate[{}]，workflowId[{}]",
                    job.asJob().getShouldFireTime().format(formatter), job.asJob().getTaskName(), ExecParamHelper.getLoadDate(execParams), job.asJob().getTask().getWorkflowId());

            // clear all sunbJobs and job
            nodeJobRepository.deleteAll(job.getSubJobs());
            jobRepository.delete(job.asJob());
            // recover trigger state
            try {
                if (scheduler.checkExists(job.asJob().getTask().getTriggerKey())) {
                    idcJobstoreCMT.updateTriggerStateToSuspended(job.asJob().getTask().getTriggerKey());
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
            // recreate job
            jobService.createJob(job.getId(), job.asJob().getTask().getTaskName(), execParams);
            // edit run param

            executeJob(jobRepository.findById(job.getId()).get());
        } else {
            // create new nodeJob instance
            NodeJob newNodeJob = new NodeJob(job.asNodeJob().getContainer(), job.asNodeJob().getNodeTask());
            logger.log(job.getId(), "重跑节点任务，oldNodeJobId[{}]，newNodeJobId[{}]，taskId[{}]，domain[{}]",
                    job.getId(), newNodeJob.getId(), job.asNodeJob().getNodeTask().getTaskId(), job.asNodeJob().getNodeTask().getDomain());
            // clear
            nodeJobRepository.delete(job.asNodeJob());
            nodeJobRepository.save(newNodeJob);
            executeNodeJob(newNodeJob);
        }
    }

    // wait for apply
    public void cancle(AbstractJob job) {
        checkRunning(job);

    }

    /**
     * @param job
     */
    public void skip(AbstractJob job) {
        checkSuccess(job);
        if (job.getTaskType() == TaskType.WORKFLOW) {
            // modify state of all subJobs of the job to skip
            job.getSubJobs().forEach(subJob -> {
                if (!subJob.getNodeId().equalsIgnoreCase(NodeTask.START) && !subJob.getNodeId().equalsIgnoreCase(NodeTask.END) && !subJob.getState().isComplete()) {
                    logger.log(subJob.getId(), "跳过节点实例，nodeJobId[{}]，taskId[{}]，domain[{}]，state[{}]"
                            , subJob.getId(), subJob.getNodeTask().getTaskId(), subJob.getNodeTask().getDomain(), subJob.getState().name());
                    subJob.setState(JobState.SKIPPED);
                }
            });
        }
        logger.log(job.getId(), "跳过任务实例，id[{}]，state[{}]", job.getId(), job.getState().name());
        modifyJobState(job, JobState.SKIPPED);
        onJobFinished(job);
    }

    public void ready(AbstractJob job) {
        checkRunning(job);
        logger.log(job.getId(), "节点任务成功派发，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                job.asNodeJob().getNodeTask().getTaskId(), job.asNodeJob().getNodeTask().getDomain(), job.getId(), job.getState().name());
        if (job.getState() == JobState.ACCEPTED) {
            modifyJobState(job, JobState.RUNNING);
        }
    }

    public void running(AbstractJob job) {
        checkRunning(job);
        logger.log(job.getId(), "节点任务正在执行，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                job.asNodeJob().getNodeTask().getTaskId(), job.asNodeJob().getNodeTask().getDomain(), job.getId(), job.getState().name());
        if (job.getState() == JobState.ACCEPTED) {
            modifyJobState(job, JobState.RUNNING);
        }
    }

    /**
     * when nodeJob have been distributed and don't callback in timeout seconds.
     * check state.if the nodeJob is ACCEPTED or RUNNING update state to fail
     * @param job
     */
    public void timeout(AbstractJob job) throws JsonProcessingException {
        // if the user  restart idc from openCallbackControl = true to openCallbackControl = false. previous timeout event exist.
        // so there do a twice validate
        if (openCallbackControl) {
            if (job.getState().isNotCallback()) {
                logger.log(job.getId(),"节点任务运行超时，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                        job.asNodeJob().getNodeTask().getTaskId(),job.asNodeJob().getNodeTask().getDomain(),job.getId(),job.getState());
                failed(job,FailMessage.newMessage(job.getId()));
            }
        }
    }

    //==============================================  this class call

    private void checkRunning(AbstractJob job) {
        if (job.getState().isComplete()) {
            throw new JobException("job:" + job.getId() + "已结束,状态为: " + job.getState());
        }
    }

    private void checkSuccess(AbstractJob job) {
        if (job.getState().isSuccess()) {
            throw new JobException("job:" + job.getId() + "已成功或跳过,状态为: " + job.getState());
        }
    }

    private void checkNotRunning(AbstractJob job) {
        if (!job.getState().isComplete()) {
            throw new JobException("任务已结束: " + job.getState());
        }
    }

    private void startJob(AbstractJob job) {
        if (job.getTaskType() == TaskType.WORKFLOW) {
            executeJob(job.asJob());
        } else {
            executeNodeJob(job.asNodeJob());
        }
    }

    private void executeJob(Job job) {
        logger.log(job.getId(), "开始执行任务实例，批次时间[{}]，taskName[{}]，jobId[{}]，loadDate[{}]，workflowId[{}]",
                job.getShouldFireTime().format(formatter)
                , job.getTask().getTaskName(), job.getId(), ExecParamHelper.getLoadDate(job.getParams()), job.getTask().getWorkflowId());
        modifyJobState(job, JobState.RUNNING);
        runNextJob(job, NodeTask.START);
    }

    private synchronized void executeNodeJob(NodeJob nodeJob) {
        NodeTask nodeTask = Objects.requireNonNull(nodeJob.getNodeTask(), "未找到任务");
        if (nodeTask.getTaskId().equalsIgnoreCase(NodeTask.CONTROL)) {
            onJobFinished(nodeJob);
            return;
        }
        if (nodeTask.getTaskId().equalsIgnoreCase(NodeTask.END) && !jobRepository.findById(nodeJob.getContainer()).get().getState().isComplete()) {
            FinishMessage message = FinishMessage.newMessage(nodeJob.getContainer());
            message.setMessage("执行结束");
            TaskEventPlugin.eventService(scheduler).send(message);
            return;
        }
        if (nodeJob.getState().equals(JobState.NONE)) {
            modifyJobState(nodeJob, JobState.ACCEPTED);
            logger.log(nodeJob.getId(), "节点任务准备派发，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]"
                    , nodeJob.getNodeTask().getTaskId(), nodeJob.getNodeTask().getDomain(), nodeJob.getId(), nodeJob.getState());
            if (openCallbackControl) {
                TaskEventPlugin.eventService(scheduler).send(TimeoutMessage.newMessage(nodeJob.getId()));
            }
            IDCJobExecutors.getExecutor().execute(execParamHelper.buildExecReq(nodeJob, nodeTask));
        }
    }

    private void runNextJob(Job job, String startNode) {
        AbstractTask task = Objects.requireNonNull(job.getTask(), "未找到任务");
        Workflow workflow = workflowRepository.findById(task.getWorkflowId()).orElseThrow(() -> new AppException("未找到指定工作流"));
        task.setWorkflow(workflow);
        // find the successors node needed to fire now
        Set<String> successors = workflow.successors(startNode);
        Iterator<NodeJob> iterator = job.getSubJobs().stream()
                .filter(sub -> successors.contains(sub.getNodeId()))
                .iterator();

        while (iterator.hasNext()) {
            NodeJob next = iterator.next();
            try {
                Set<String> previous = workflow.getPrevious(next.getNodeId());

                // if  the job's all previous jobs don't complete ,then skip this fire
                boolean unfinishJob = job.getSubJobs().stream()
                        .filter(sub -> !sub.isSystemNode() && previous.contains(sub.getNodeId()))
                        .anyMatch(sub -> !sub.getState().isSuccess());
                if (!unfinishJob) {
                    startJob(next);
                }
            } catch (Exception e) {
                e.printStackTrace();
                modifyJobState(next, JobState.FAILED);
                modifyJobState(job, JobState.FAILED);
            }
        }
    }

    private void onJobFinished(AbstractJob runningJob) {
        if (runningJob instanceof Job) {
            // Release trigger
            Job job = runningJob.asJob();
            TriggerKey tk = job.getTask().getTriggerKey();
            if (job.getState().isComplete()) {
                if (job.getState().isSuccess()) {
                    idcJobStore.releaseTrigger(tk, ReleaseInstruction.RELEASE);
                } else {
                    idcJobStore.releaseTrigger(tk, ReleaseInstruction.SET_ERROR);
                }
            }
            logger.log(job.getId(), "节点任务执行完毕，批次时间[{}]，taskName[{}]，jobId[{}]，loadDate[{}]，workflowId[{}]，state[{}]",
                    job.getShouldFireTime().format(formatter), job.getTask().getTaskName(), job.getId(), ExecParamHelper.getLoadDate(job.getParams()), job.getTask().getWorkflowId(), job.getState().name());
        } else {
            NodeJob nodeJob = runningJob.asNodeJob();
            Workflow workflow = workflowRepository.findById(nodeJob.getWorkflowId()).get();
            Job parent = jobRepository.findById(nodeJob.getContainer()).get();
            parent.getTask().setWorkflow(workflow);
            logger.log(nodeJob.getId(), "节点任务执行完毕，批次时间[{}]，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                    parent.getShouldFireTime().format(formatter), nodeJob.getNodeTask().getTaskId(), nodeJob.getNodeTask().getDomain(), nodeJob.getId(), nodeJob.getState());
            runNextJob(parent, nodeJob.getNodeId());
        }
    }

    public void modifyJobState(AbstractJob job, JobState state) {
        job.setState(state);
        if (job.getState().equals(JobState.RUNNING)) {
            job.setStarttime(LocalDateTime.now());
            job.setUpdatetime(null);
        } else {
            job.setUpdatetime(LocalDateTime.now());
        }
        allJobRepository.save(job);
    }

}
