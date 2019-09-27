package com.iwellmass.idc.app.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Queues;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.message.*;
import com.iwellmass.idc.scheduler.IDCJobExecutors;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.quartz.IDCJobStore;
import com.iwellmass.idc.scheduler.quartz.IDCJobstoreCMT;
import com.iwellmass.idc.scheduler.quartz.ReleaseInstruction;
import com.iwellmass.idc.scheduler.repository.*;
import com.iwellmass.idc.scheduler.service.IDCLogger;
import lombok.Setter;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * @author nobita chen
 * @email nobita0522@qq.com
 * @date 2019/6/25 15:04
 */
@Component
public class JobHelper {

    static Logger LOGGER = LoggerFactory.getLogger(JobHelper.class);
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
//    @Inject
//    IDCPluginRepository idcPluginRepository;
    @Value(value = "${idc.scheduler.openCallbackControl:false}")
    boolean openCallbackControl;
    @Value(value = "${idc.scheduler.maxRunningJobs:10}")
    private Integer maxRunningJobs;
    BlockingQueue<NodeJob> nodeJobWaitQueue = Queues.newLinkedBlockingQueue();

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
        checkRunning(abstractJob);
        modifyJobState(abstractJob, JobState.FINISHED);
        onJobFinished(abstractJob);
        // notify wait queue
        notifyWaitQueue();
    }

    public void failed(AbstractJob abstractJob, JobMessage message) {
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
                    message.getThrowable() == null ? null : message.getStackTrace(),
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
        // notify wait queue
        notifyWaitQueue();

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
                    Objects.nonNull(job.asJob().getShouldFireTime()) ? job.asJob().getShouldFireTime().format(formatter) : null, job.asJob().getTaskName(), ExecParamHelper.getLoadDate(execParams), job.asJob().getTask().getWorkflowId());

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
            // recreate job:the task info will lose,
            jobService.createJob(job.getId(), job.asJob().getTask().getTaskName(), execParams, job.asJob().getShouldFireTime());
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
        notifyWaitQueue();
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
        // notify wait queue
        notifyWaitQueue();
    }

    public void ready(AbstractJob job) {
        checkRunning(job);
        logger.log(job.getId(), "节点任务成功派发，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                job.asNodeJob().getNodeTask().getTaskId(), job.asNodeJob().getNodeTask().getDomain(), job.getId(), job.getState().name());
        if (job.getState() == JobState.ACCEPTED) {
            modifyJobState(job, JobState.RUNNING);
        }
    }

    public void running(AbstractJob job,JobMessage jobMessage) {
        checkRunning(job);
        if (Strings.isNullOrEmpty(jobMessage.getMessage())) {
            logger.log(job.getId(), "节点任务正在执行，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                    job.asNodeJob().getNodeTask().getTaskId(), job.asNodeJob().getNodeTask().getDomain(), job.getId(), job.getState().name());
        } else {
            logger.log(job.getId(), "节点任务正在执行，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]，detail[{}]",
                    job.asNodeJob().getNodeTask().getTaskId(), job.asNodeJob().getNodeTask().getDomain(), job.getId(), job.getState().name(),jobMessage.getMessage());
        }

        if (job.getState() == JobState.ACCEPTED) {
            modifyJobState(job, JobState.RUNNING);
        }
    }

    /**
     * when nodeJob have been distributed and don't callback in timeout seconds.
     * check state.if the nodeJob is ACCEPTED or RUNNING update state to fail
     *
     * @param job
     */
    public void timeout(AbstractJob job) {
        // if the user  restart idc from openCallbackControl = true to openCallbackControl = false. previous timeout event exist.
        // so there do a twice validate
        if (openCallbackControl) {
            if (job.getState().isNotCallback()) {
                logger.log(job.getId(), "节点任务运行超时，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                        job.asNodeJob().getNodeTask().getTaskId(), job.asNodeJob().getNodeTask().getDomain(), job.getId(), job.getState());
                failed(job, FailMessage.newMessage(job.getId()));
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
                Objects.nonNull(job.getShouldFireTime()) ? job.getShouldFireTime().format(formatter) : null
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
        if (nodeTask.getTaskId().equalsIgnoreCase(NodeTask.END)) {
            FinishMessage message = FinishMessage.newMessage(nodeJob.getContainer());
            message.setMessage("执行结束");
            TaskEventPlugin.eventService(scheduler).send(message);
            return;
        }
        if (nodeJob.getState().equals(JobState.NONE)) {
            // concurrent control
            if (canDispatch()) {
                modifyJobState(nodeJob, JobState.ACCEPTED);
                logger.log(nodeJob.getId(), "节点任务准备派发，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]"
                        , nodeJob.getNodeTask().getTaskId(), nodeJob.getNodeTask().getDomain(), nodeJob.getId(), nodeJob.getState());
                if (openCallbackControl) {
                    TaskEventPlugin.eventService(scheduler).send(TimeoutMessage.newMessage(nodeJob.getId()));
                }
                IDCJobExecutors.getExecutor().execute(execParamHelper.buildExecReq(nodeJob, nodeTask));
            } else {
                // concurrent control plan 1:by quartz;The efficiency of this plan is low.
//                StartMessage startMessage = StartMessage.newMessage(nodeJob.getId());
//                startMessage.setMessage("并发控制被阻塞,nodJobId:" + nodeJob.getId());
//                TaskEventPlugin.eventService(scheduler).send(startMessage);

                // concurrent control plan 2:by blockingQueue and notify principle
                // add this nodeJob to wait queue
                addNodeJobToWaitQueue(nodeJob);
            }
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


    /**
     * used for concurrent control.
     *
     * @return true:canDispatch
     */
    private boolean canDispatch() {
        return nodeJobRepository.countNodeJobsByRunningOrAcceptState() < maxRunningJobs;
    }

    /**
     * notify wait queue to release the wait-nodeJobs .need check whether we can release
     */
    private synchronized void notifyWaitQueue() {
        int needReleaseJobs = maxRunningJobs - nodeJobRepository.countNodeJobsByRunningOrAcceptState();
        if (needReleaseJobs > 0) {
            // release waiting job
            if (!nodeJobWaitQueue.isEmpty()) {
                for (int i = 0;i < Math.min(needReleaseJobs,nodeJobWaitQueue.size());i++) {
                    try {
                        NodeJob nodeJob = nodeJobWaitQueue.take();
                        LOGGER.info("任务出队：nodeJob[{}]",nodeJob.getId());
                        executeNodeJob(nodeJob);
                    } catch (InterruptedException e) {
                        LOGGER.error("NodeJob等待队列出队异常");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void addNodeJobToWaitQueue(NodeJob nodeJob) {
        try {
            LOGGER.info("达到最大并发数，任务入队：nodeJob[{}]",nodeJob.getId());
            nodeJobWaitQueue.put(nodeJob);
        } catch (InterruptedException e) {
            LOGGER.error("NodeJob等待队列入队异常:nodeJob[{}]",nodeJob.getId());
            e.printStackTrace();
        }
    }


    public void modifyConcurrent(Integer maxRunningJobs) {
        if (maxRunningJobs <= 0) {
            throw new AppException("illegal param:maxRunningJobs must greater than 0");
        }
        this.maxRunningJobs = maxRunningJobs;
    }

    /**
     * 出错重试
     * @param nodeJob
     */
    public void retry(NodeJob nodeJob) {




    }

}
