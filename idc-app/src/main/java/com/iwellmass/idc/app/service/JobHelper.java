package com.iwellmass.idc.app.service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.message.*;
import com.iwellmass.idc.scheduler.IDCJobExecutors;
import com.iwellmass.idc.scheduler.model.*;
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
import java.util.concurrent.atomic.AtomicInteger;

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
    @Value(value = "${idc.scheduler.maxRunningJobs:10}")
    private Integer maxRunningJobs;
    @Value(value = "${idc.scheduler.retryCount:3}")
    private Integer retryCount;

    // todo below need to persistence
    private BlockingQueue<String> nodeJobWaitQueue = Queues.newLinkedBlockingQueue(); // nodeJobId
    private Map<String, AtomicInteger> nodeJobRetryCount = Maps.newConcurrentMap();// key -> nodeJobId, value -> the count of nodeJob run
    private Map<String, HashSet<String>> nodeJobPausedMap = new HashMap<>(); // key -> containerId ,value ->   paused nodeJobIds

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
        // checkRunning(abstractJob); // con't directly adopt checkRunning(abstractJob);if we choose skip wii trigger parent success.it will fail
        if (!abstractJob.isJob()) {
            checkRunning(abstractJob);
        }
        modifyJobState(abstractJob, JobState.FINISHED);
        onJobFinished(abstractJob);
        if (!abstractJob.isJob()) {  // this flush must be after onJobFinished.because onJobFinish method dependent on job'state.we first need call onJobFinish then valdiate job'state
            flushParentStateAndHandleTriggerState(abstractJob.asNodeJob());
        } else {
            flushJobStateAndHandleTriggerState(abstractJob.asJob());
        }
        // notify wait queue
        notifyWaitQueue();
    }

    public void failed(AbstractJob abstractJob, JobMessage message) {
        checkRunning(abstractJob);
        modifyJobState(abstractJob, JobState.FAILED);
        if (!abstractJob.isJob() && getTaskByNodeJob(abstractJob.asNodeJob()).getIsRetry()) {
            // only support retry nodeJob
            if (!nodeJobRetryCount.containsKey(abstractJob.getId())) {
                nodeJobRetryCount.put(abstractJob.getId(), new AtomicInteger(1));
                retry(abstractJob.asNodeJob(), message);
                return;
            } else if (nodeJobRetryCount.containsKey(abstractJob.getId()) && nodeJobRetryCount.get(abstractJob.getId()).getAndIncrement() < retryCount) {
                retry(abstractJob.asNodeJob(), message);
                return;
            } else {
                // nodeJobRetryCount.get(abstractJob.getId()).getAndIncrement() >= retryCount
                nodeJobRetryCount.remove(abstractJob.getId());
            }
        }
        if (abstractJob.isJob()) {
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
            Job parent = getParentByNodeJob(abstractJob.asNodeJob());
//            // caller of this method may by redo method.before redo.the parent'state is likely to be complete.
//            // only the parent's state is running.we need modify parent's state
//            if (parent.getState().equals(JobState.RUNNING)) {
//                modifyJobState(parent, JobState.FAILED);
//            }
            flushParentStateAndHandleTriggerState(abstractJob.asNodeJob());
            ExecutionLog jobExecutionLog = ExecutionLog.createLog(parent.getId(), "任务实例执行失败，批次时间[{}]，taskName[{}]，jobId[{}]，loadDate[{}]，workflowId[{}]",
                    null,
                    parent.getShouldFireTime().format(formatter), parent.getTask().getTaskName(), parent.getId(), parent.getLoadDate(), parent.getTask().getWorkflowId());
            logger.log(jobExecutionLog);
        }
        if (!abstractJob.isJob() && !(getTaskByNodeJob(abstractJob.asNodeJob()).getBlockOnError())) {
            onJobFinished(abstractJob);
            flushParentStateAndHandleTriggerState(abstractJob.asNodeJob());
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

            // recreate job:the task info will lose,so we need get info from the last job
            jobService.createJob(job.getId(), job.asJob().getTask().getTaskName(), execParams, job.asJob().getShouldFireTime());

            Job newJob = jobRepository.findById(job.getId()).get();
            // edit run param
            executeJob(newJob);
            flushJobStateAndHandleTriggerState(newJob);
        } else {
            // create new nodeJob instance
            NodeJob newNodeJob = new NodeJob(job.asNodeJob().getContainer(), job.asNodeJob().getNodeTask());
            logger.log(job.getId(), "重跑节点任务，oldNodeJobId[{}]，newNodeJobId[{}]，taskId[{}]，domain[{}]",
                    job.getId(), newNodeJob.getId(), job.asNodeJob().getNodeTask().getTaskId(), job.asNodeJob().getNodeTask().getDomain());
            // clear
            nodeJobRepository.delete(job.asNodeJob());
            nodeJobRepository.save(newNodeJob);
            executeNodeJob(newNodeJob);
            flushParentStateAndHandleTriggerState(newNodeJob);
        }
    }

    // wait for apply
    public void cancel(AbstractJob job) {
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
                if (!subJob.getState().isSuccess()) {
                    logger.log(subJob.getId(), "跳过节点实例，nodeJobId[{}]，taskId[{}]，domain[{}]，state[{}]"
                            , subJob.getId(), subJob.getNodeTask().getTaskId(), subJob.getNodeTask().getDomain(), subJob.getState().name());
                    subJob.setState(JobState.SKIPPED);
                }
            });
        }
        logger.log(job.getId(), "跳过任务实例，id[{}]，state[{}]", job.getId(), job.getState().name());
        modifyJobState(job, JobState.SKIPPED);
        onJobFinished(job);
        if (!job.isJob()) {
            flushParentStateAndHandleTriggerState(job.asNodeJob());
        } else {
            flushJobStateAndHandleTriggerState(job.asJob());
        }
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

    public void running(AbstractJob job, JobMessage jobMessage) {
        checkRunning(job);
        if (Strings.isNullOrEmpty(jobMessage.getMessage())) {
            logger.log(job.getId(), "节点任务正在执行，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                    job.asNodeJob().getNodeTask().getTaskId(), job.asNodeJob().getNodeTask().getDomain(), job.getId(), job.getState().name());
        } else {
            logger.log(job.getId(), "节点任务正在执行，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]，detail[{}]",
                    job.asNodeJob().getNodeTask().getTaskId(), job.asNodeJob().getNodeTask().getDomain(), job.getId(), job.getState().name(), jobMessage.getMessage());
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
            if (job.getState().isRunning()) {
                logger.log(job.getId(), "节点任务运行超时，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                        job.asNodeJob().getNodeTask().getTaskId(), job.asNodeJob().getNodeTask().getDomain(), job.getId(), job.getState());
                failed(job, FailMessage.newMessage(job.getId()));
            }
        }
    }

    /**
     * 出错重试
     *
     * @param nodeJob
     */
    public void retry(NodeJob nodeJob, JobMessage message) {
        LOGGER.info("NodeJob执行失败，失败重试第{}次，nodeJob[{}]", nodeJobRetryCount.get(nodeJob.getId()).get(), nodeJob.getId());
        ExecutionLog nodeJobExecutionLog = ExecutionLog.createLog(nodeJob.getId(), "节点任务执行失败，失败重试第{}次，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                message.getThrowable() == null ? null : message.getStackTrace(),
                nodeJobRetryCount.get(nodeJob.getId()).get(), nodeJob.getNodeTask().getTaskId(), nodeJob.getNodeTask().getDomain(), nodeJob, nodeJob.getState().name());
        logger.log(nodeJobExecutionLog);
        modifyJobState(nodeJob, JobState.NONE);
        executeNodeJob(nodeJob);
        flushParentStateAndHandleTriggerState(nodeJob);
    }

    private NodeJob findStartNodeJob(String containerId) {
        return nodeJobRepository.findAllByContainer(containerId).stream().
                filter(nj -> nj.getNodeId().equalsIgnoreCase(NodeTask.START)).
                findFirst().
                orElseThrow(() -> new AppException("未找到指定containerId下的startJob"));
    }

    public void forceComplete(String nodeJodId) {
        NodeJob nodeJob = nodeJobRepository.findById(nodeJodId).orElseThrow(() -> new AppException("未查找到指定nodeJobId的实例：" + nodeJodId));
        modifyJobState(nodeJob, JobState.FINISHED);
        onJobFinished(nodeJob);
        flushParentStateAndHandleTriggerState(nodeJob);
    }

    /**
     * pause the job.
     *
     * @param jobId jobId
     */
    public synchronized void pause(String jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new AppException("未发现jobId[%s]的实例任务", jobId));
        if (job.getState().isPaused()) {
            throw new AppException("该实例job[%s]已暂停", jobId);
        }
        if (job.getSubJobs().stream().noneMatch(nd -> nd.getState().equals(JobState.NONE) &&
                nodeJobWaitQueue.stream().noneMatch(nodeJobId -> nodeJobId.equals(nd.getId())))) {
            throw new AppException("暂停失败，该实例不存在可以暂停的节点任务");
        }
        modifyJobState(job, JobState.PAUSED);
        // modify those job whose state is none and isn't in waitQueue.
        job.getSubJobs().forEach(nd -> {
            if (nd.getState().equals(JobState.NONE) && nodeJobWaitQueue.stream().noneMatch(nodeJobId -> nodeJobId.equals(nd.getId()))) {
                modifyJobState(nd, JobState.PAUSED);
            }
        });
        flushJobStateAndHandleTriggerState(job);
    }

    /**
     * resume the paused job.
     *
     * @param jobId jobId
     */
    public synchronized void resume(String jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new AppException("未发现jobId[%s]的实例任务", jobId));
        if (!job.getState().equals(JobState.PAUSED)) {
            throw new AppException("该实例job[%s]未暂停", jobId);
        }
        // notify those paused nodeJob.
        resumePausedNodeJobs(job);
        flushJobStateAndHandleTriggerState(job);
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

    private void startJob(AbstractJob job) {
        if (job.getTaskType() == TaskType.WORKFLOW) {
            executeJob(job.asJob());
        } else {
            executeNodeJob(job.asNodeJob());
        }
    }

    private synchronized void executeJob(Job job) {
        logger.log(job.getId(), "开始执行任务实例，批次时间[{}]，taskName[{}]，jobId[{}]，loadDate[{}]，workflowId[{}]",
                Objects.nonNull(job.getShouldFireTime()) ? job.getShouldFireTime().format(formatter) : null
                , job.getTask().getTaskName(), job.getId(), ExecParamHelper.getLoadDate(job.getParams()), job.getTask().getWorkflowId());
        NodeJob startNodeJob = findStartNodeJob(job.getId());
        modifyJobState(startNodeJob, JobState.FINISHED);
        onJobFinished(startNodeJob);
        flushParentStateAndHandleTriggerState(startNodeJob);
    }

    private synchronized void executeNodeJob(NodeJob nodeJob) {
        NodeTask nodeTask = Objects.requireNonNull(nodeJob.getNodeTask(), "未找到任务");
        if (!nodeJob.getState().isPaused()) {
            if (getParentByNodeJob(nodeJob).getState().equals(JobState.RUNNING) && !nodeJob.getState().equals(JobState.NONE)) {
                LOGGER.info("nodeJob[{}]不是初始化状态:state{} ", nodeJob.getId(), nodeJob.getState());
                return;
            }
            if (nodeTask.getTaskId().equalsIgnoreCase(NodeTask.CONTROL)) {
                modifyJobState(nodeJob, JobState.FINISHED);
                onJobFinished(nodeJob);
                flushParentStateAndHandleTriggerState(nodeJob);
                return;
            }
            if (nodeTask.getTaskId().equalsIgnoreCase(NodeTask.END)) {
                modifyJobState(nodeJob, JobState.FINISHED);
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
        } else {
            // add this nodeJob to paused map
            addNodeJobToPausedMap(nodeJob);
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
                LOGGER.info("Job[{}]是否开启出错阻塞：{}", job.getId(), job.getTask().getBlockOnError());
                // if  the job's all previous jobs don't complete ,then skip this fire
                boolean unfinishJob = job.getSubJobs().stream()
                        .filter(sub -> !sub.isSystemNode() && previous.contains(sub.getNodeId()))
                        .anyMatch(sub -> job.getTask().getBlockOnError() ? !sub.getState().isSuccess() : !sub.getState().isComplete());
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
            logger.log(job.getId(), "节点任务执行完毕，批次时间[{}]，taskName[{}]，jobId[{}]，loadDate[{}]，workflowId[{}]，state[{}]",
                    job.getShouldFireTime().format(formatter), job.getTask().getTaskName(), job.getId(), ExecParamHelper.getLoadDate(job.getParams()), job.getTask().getWorkflowId(), job.getState().name());
        } else {
            NodeJob nodeJob = runningJob.asNodeJob();
            Workflow workflow = workflowRepository.findById(nodeJob.getWorkflowId()).get();
            Job parent = getParentByNodeJob(nodeJob);
            parent.getTask().setWorkflow(workflow);
            logger.log(nodeJob.getId(), "节点任务执行完毕，批次时间[{}]，taskId[{}]，domain[{}]，nodeJobId[{}]，state[{}]",
                    parent.getShouldFireTime().format(formatter), nodeJob.getNodeTask().getTaskId(), nodeJob.getNodeTask().getDomain(), nodeJob.getId(), nodeJob.getState());
            runNextJob(parent, nodeJob.getNodeId());
        }
    }

    private synchronized void modifyJobState(AbstractJob job, JobState state) {
        if (!state.equals(job.getState())) {
            job.setState(state);
        }
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
     * @return true:the nodeJob can dispatch
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
            for (int i = 0; i < Math.min(needReleaseJobs, nodeJobWaitQueue.size()); i++) {
                if (!nodeJobWaitQueue.isEmpty()) {
                    try {
                        Optional<NodeJob> nodeJobInWaitQueue = nodeJobRepository.findById(nodeJobWaitQueue.take()); // this job'state could be illegal;  need validate
                        if (nodeJobInWaitQueue.isPresent()) {
                            LOGGER.info("任务出队：nodeJob[{}]", nodeJobInWaitQueue.get().getId());
                            if (nodeJobInWaitQueue.get().getState().equals(JobState.NONE)) {
                                // this operation will lose one callback of message.so we adopt i-- to offset this operation and need twice check by nodeJobWaitQueue.isEmpty()
                                executeNodeJob(nodeJobInWaitQueue.get());
                            } else {
                                i--;
                            }
                        } else {
                            i--;
                        }
                    } catch (InterruptedException e) {
                        LOGGER.error("NodeJob等待队列出队异常");
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
    }

    private void addNodeJobToWaitQueue(NodeJob nodeJob) {
        try {
            LOGGER.info("达到最大并发数，任务入队：nodeJob[{}]", nodeJob.getId());
            nodeJobWaitQueue.put(nodeJob.getId());
        } catch (InterruptedException e) {
            LOGGER.error("NodeJob等待队列入队异常:nodeJob[{}]", nodeJob.getId());
            e.printStackTrace();
        }
    }

    public void modifyConcurrent(Integer maxRunningJobs) {
        if (maxRunningJobs < 1) {
            throw new AppException("illegal param:maxRunningJobs must greater than 0");
        }
        this.maxRunningJobs = maxRunningJobs;
    }

    public void modifyRetryCount(Integer retryCount) {
        if (retryCount < 1) {
            throw new AppException("illegal param:retryCount must greater than 0");
        }
        this.retryCount = retryCount;
    }

    private Task getTaskByNodeJob(NodeJob nodeJob) {
        return jobRepository.findById(nodeJob.getContainer()).orElseThrow(() -> new AppException("未找到指定job实例" + nodeJob.getContainer())).getTask();
    }

    /**
     * when a message reach and the nodeJob'state need to be changed. we need to flush the state of nodeJob's parent and the trigger.
     * 1.when the job open blockOnError. a nodeJob fail then the parent fail
     * 2.when the job close blockOnError.all nodeJob finish .we need update parent'state to complete.
     * <p>
     * job state:
     * 1.fail:when all subJobs was done and there exist one and more subJobs is fail,the job'state is fail
     * 2.success: all subJobs was done and all subJobs was success,the job'state is success
     * 3.running:when there exist one nodeJob is running ,the job'state is running.
     * <p>
     * attention:the caller of this method must after modifyJobState and onJobFinished and runNextJob.
     *
     * @param nodeJob the nodeJob
     */
    private synchronized void flushParentStateAndHandleTriggerState(NodeJob nodeJob) {
        flushJobStateAndHandleTriggerState(getParentByNodeJob(nodeJob));
    }

    private synchronized void flushJobStateAndHandleTriggerState(Job job) {
        // indicate whether the job is the latest job
        // if the job is the latest job.we should sync trigger's state.
        boolean isLatestJob = getLatestJobByTaskName(job.getTaskName()).getId().equals(job.getId());
        // flush job state
        List<NodeJob> subJobs = job.getSubJobs();
        if (subJobs.isEmpty()) {
            modifyJobState(job, JobState.NONE);
            return;
        }

        if (subJobs.stream().anyMatch(nd -> nd.getState().isPaused())) {
            modifyJobState(job, JobState.PAUSED);
            return;
        }

        // when there exist one running job or exist one nodejob in nodejobWaitQueue,the job'state is running or paused
        // attention:state of the job may be running or paused.if the state is paused.need modifyJobState and validate job'state before this method.
        boolean inNodeJobWaitQueue = false;
        for (NodeJob nodeJob : subJobs) {
            if (!nodeJob.getState().isComplete() && nodeJobWaitQueue.contains(nodeJob.getId())) {
                inNodeJobWaitQueue = true;
                break;
            }
        }
        if (inNodeJobWaitQueue || subJobs.stream().anyMatch(nd -> nd.getState().isRunning())) {
            if (!job.getState().equals(JobState.RUNNING)) {
                modifyJobState(job, JobState.RUNNING);
                if (isLatestJob) {
                    try {
                        if (scheduler.checkExists(job.getTask().getTriggerKey())) {
                            idcJobstoreCMT.updateTriggerStateToSuspended(job.asJob().getTask().getTriggerKey());
                        }
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }
        if (subJobs.stream().anyMatch(nd -> nd.getState().isFailure())) {
            modifyJobState(job, JobState.FAILED);
            if (isLatestJob) {
                TriggerKey tk = job.getTask().getTriggerKey();
                try {
                    if (scheduler.checkExists(tk)) {
                        if (job.getTask().getBlockOnError()) {
                            idcJobstoreCMT.releaseTrigger(tk, ReleaseInstruction.SET_ERROR);
                        } else if (job.getSubJobs().stream().allMatch(nd -> nd.getState().isComplete())) {
                            idcJobstoreCMT.releaseTrigger(tk, ReleaseInstruction.RELEASE);
                        }
                    }
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        if (subJobs.stream().allMatch(nd -> nd.getState().isSuccess())) {
            modifyJobState(job, JobState.FINISHED);
            if (isLatestJob) {
                TriggerKey tk = job.getTask().getTriggerKey();
                try {
                    if (scheduler.checkExists(tk)) {
                        idcJobstoreCMT.releaseTrigger(tk, ReleaseInstruction.RELEASE);
                    }
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Job getParentByNodeJob(NodeJob nodeJob) {
        return jobRepository.findById(nodeJob.getContainer())
                .orElseThrow(() -> new AppException("未能找到nodeJob的parent,nodeJob[%s],container[%s]", nodeJob.getId(), nodeJob.getContainer()));
    }

    private void addNodeJobToPausedMap(NodeJob nodeJob) {
        LOGGER.info("job[{}]暂停,nodeTask[{}],nodeJob[{}]进入暂停等待区", nodeJob.getContainer(), nodeJob.getNodeTask().getTaskName(), nodeJob.getId());
        HashSet<String> nodeJobsInPausedMap = nodeJobPausedMap.getOrDefault(nodeJob.getContainer(), Sets.newHashSet());
        nodeJobsInPausedMap.add(nodeJob.getId());
        nodeJobPausedMap.put(nodeJob.getContainer(), nodeJobsInPausedMap);
    }

    private TriggerKey getTriggerKeyByNodeJob(NodeJob nodeJob) {
        return jobRepository.findById(nodeJob.getContainer())
                .orElseThrow(() -> new AppException("未发现jobId[%s]的实例任务", nodeJob.getContainer()))
                .getTask().getTriggerKey();
    }

    // get the latest job of this task.
    private Job getLatestJobByTaskName(String taskName) {
        return jobRepository.findAllByTaskName(taskName).stream().
                sorted((o1, o2) -> o1.getShouldFireTime().isAfter(o2.getShouldFireTime()) ? -1 : 1)
                .findFirst().get();
    }

    private void resumePausedNodeJobs(Job job) {
        job.getSubJobs().forEach(nd -> {
            if (nd.getState().equals(JobState.PAUSED)) {
                modifyJobState(nd, JobState.NONE);
            }
        });

        nodeJobPausedMap.getOrDefault(job.getId(), Sets.newHashSet())
                .forEach(nodeJobId -> nodeJobRepository.findById(nodeJobId).ifPresent(this::executeNodeJob));
        nodeJobPausedMap.remove(job.getId());
    }

}
