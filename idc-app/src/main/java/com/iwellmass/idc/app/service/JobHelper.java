package com.iwellmass.idc.app.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Inject
    TaskDependencyEdgeRepository taskDependencyEdgeRepository;
    @Inject
    TaskDependencyRepository taskDependencyRepository;
    @Inject
    TaskRepository taskRepository;

    @Value(value = "${idc.scheduler.openCallbackControl:false}")
    boolean openCallbackControl;
    @Value(value = "${idc.scheduler.maxRunningJobs:10}")
    private Integer maxRunningJobs;
    @Value(value = "${idc.scheduler.retryCount:3}")
    private Integer retryCount;

    // todo below need to persistence(store into database)
    private BlockingQueue<String> nodeJobWaitQueue = Queues.newLinkedBlockingQueue(); // nodeJobId
    private Map<String, AtomicInteger> nodeJobRetryCount = Maps.newConcurrentMap();// key -> nodeJobId, value -> the count of nodeJob run
    private Map<String, HashSet<String>> nodeJobPausedMap = Maps.newConcurrentMap(); // key -> containerId ,value ->  those nodeJob which call executeNodeJob method and then should exec after resume.
    private Set<String> jobWaitSet = Sets.newConcurrentHashSet(); // jobId that waited because of dependencies between task.

    //==============================================  processor call

    public void start(AbstractJob abstractJob) {
        if (abstractJob.getState().isComplete()) {
            throw new JobException("任务已执行");
        }
        if (abstractJob.getTask() == null) {
            throw new JobException("任务不存在");
        }
        if (abstractJob.getTaskType() == TaskType.WORKFLOW) {
            Job job = abstractJob.asJob();
            if (jobCanExecByTaskDependency(job) && jobCanExecByMaxBatch(job)) {
                executeJob(job);
            } else {
                LOGGER.info("taskName[{}],Job[{}],batchTime[{}]由于调度计划依赖或最大批次被阻塞", job.getTaskName(), job.getId(), job.getBatchTime().toString());
                jobWaitSet.add(job.getId());
            }
        } else {
            executeNodeJob(abstractJob.asNodeJob());
        }
    }

    public void success(AbstractJob abstractJob) {
        // checkRunning(abstractJob); // con't directly adopt checkRunning(abstractJob);if we choose skip wii trigger parent success.it will fail
        if (!abstractJob.isJob()) {
            checkRunning(abstractJob);
        }
        modifyJobState(abstractJob, JobState.FINISHED);
        onJobFinished(abstractJob);
        if (!abstractJob.isJob()) {
            flushParentStateAndHandleTriggerState(abstractJob.asNodeJob());
        } else {
            notifyJobInWaitSet();
            flushJobStateAndHandleTriggerState(abstractJob.asJob());
        }
        // notify wait nodeJob
        notifyWaitNodeJob();
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
            ExecutionLog nodeJobExecutionLog = ExecutionLog.createLog(abstractJob.getId(), "节点任务执行失败，taskId[{}]，nodeJobId[{}]，state[{}]",
                    message.getThrowable() == null ? null : message.getStackTrace(),
                    abstractJob.asNodeJob().getNodeTask().getTaskId(), abstractJob.getId(), abstractJob.getState().name());
            logger.log(nodeJobExecutionLog);
            Job parent = getParentByNodeJob(abstractJob.asNodeJob());
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
        notifyWaitNodeJob();

    }

    /**
     * redo job:adapt different strategy with the kind of job ,the kind of absJob contain nodeJob and job
     * when job is nodeJob: clear the nodeJob and generate a new nodeJob instance,the new nodeJob instance contain new jobId,but the containnerId must use oldJod's containerId
     * when job is task's instance,job : the jobId can't be generated then clear all subJobs of the job and recreate all job's subJobs
     *
     * @param job
     */
    public void redo(AbstractJob job) {
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
            start(newJob);
//            flushJobStateAndHandleTriggerState(newJob);
        } else {
            // create new nodeJob instance
            NodeJob newNodeJob = new NodeJob(job.asNodeJob().getContainer(), job.asNodeJob().getNodeTask());
            logger.log(job.getId(), "重跑节点任务，oldNodeJobId[{}]，newNodeJobId[{}]，taskId[{}]",
                    job.getId(), newNodeJob.getId(), job.asNodeJob().getNodeTask().getTaskId());
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
        notifyWaitNodeJob();
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
                    logger.log(subJob.getId(), "跳过节点实例，nodeJobId[{}]，taskId[{}]，state[{}]"
                            , subJob.getId(), subJob.getNodeTask().getTaskId(), subJob.getState().name());
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
            notifyJobInWaitSet();
            flushJobStateAndHandleTriggerState(job.asJob());
        }
        // notify wait queue
        notifyWaitNodeJob();
    }

    public void ready(AbstractJob job) {
        checkRunning(job);
        logger.log(job.getId(), "节点任务成功派发，taskId[{}]，nodeJobId[{}]，state[{}]",
                job.asNodeJob().getNodeTask().getTaskId(), job.getId(), job.getState().name());
        if (job.getState() == JobState.ACCEPTED) {
            modifyJobState(job, JobState.RUNNING);
        }
    }

    public void running(AbstractJob job, JobMessage jobMessage) {
        checkRunning(job);
        if (Strings.isNullOrEmpty(jobMessage.getMessage())) {
            logger.log(job.getId(), "节点任务正在执行，taskId[{}]，nodeJobId[{}]，state[{}]",
                    job.asNodeJob().getNodeTask().getTaskId(), job.getId(), job.getState().name());
        } else {
            logger.log(job.getId(), "节点任务正在执行，taskId[{}]，nodeJobId[{}]，state[{}]，detail[{}]",
                    job.asNodeJob().getNodeTask().getTaskId(), job.getId(), job.getState().name(), jobMessage.getMessage());
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
                logger.log(job.getId(), "节点任务运行超时，taskId[{}]，nodeJobId[{}]，state[{}]",
                        job.asNodeJob().getNodeTask().getTaskId(), job.getId(), job.getState());
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
        ExecutionLog nodeJobExecutionLog = ExecutionLog.createLog(nodeJob.getId(), "节点任务执行失败，失败重试第{}次，taskId[{}]，nodeJobId[{}]，state[{}]",
                message.getThrowable() == null ? null : message.getStackTrace(),
                nodeJobRetryCount.get(nodeJob.getId()).get(), nodeJob.getNodeTask().getTaskId(), nodeJob, nodeJob.getState().name());
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
        AbstractJob abstractJob = allJobRepository.findById(jobId).orElseThrow(() -> new AppException("未发现jobId[%s]的实例任务", jobId));
        if (abstractJob.getState().isPaused()) {
            throw new AppException("该实例job[%s]已暂停", jobId);
        }
        if (abstractJob.isJob()) {
            if (abstractJob.getSubJobs().stream().noneMatch(nd -> nd.getState().equals(JobState.NONE))) {
                throw new AppException("暂停失败，该实例不存在可以暂停的节点任务");
            }
            modifyJobState(abstractJob, JobState.PAUSED);
            // modify those job whose state is none and isn't in waitQueue.
            abstractJob.getSubJobs().forEach(nd -> {
                if (nd.getState().equals(JobState.NONE)) {
                    modifyJobState(nd, JobState.PAUSED);
                }
            });
            flushJobStateAndHandleTriggerState(abstractJob.asJob());
        } else {
            if (!abstractJob.getState().isNone()) {
                throw new AppException("该实例nodeJob[%s]未处于初始化状态", jobId);
            }
            modifyJobState(abstractJob, JobState.PAUSED);
            flushParentStateAndHandleTriggerState(abstractJob.asNodeJob());
        }
    }

    /**
     * resume the paused job.
     *
     * @param jobId jobId
     */
    public synchronized void resume(String jobId) {
        AbstractJob abstractJob = allJobRepository.findById(jobId).orElseThrow(() -> new AppException("未发现jobId[%s]的实例任务", jobId));
        if (!abstractJob.getState().isPaused()) {
            throw new AppException("该实例job[%s]未暂停", jobId);
        }
        if (abstractJob.isJob()) {
            // notify those paused nodeJob.
            resumePausedNodeJobsByJob(abstractJob.asJob());
            flushJobStateAndHandleTriggerState(abstractJob.asJob());
        } else {
            resumePausedNodeJob(abstractJob.asNodeJob());
            flushParentStateAndHandleTriggerState(abstractJob.asNodeJob());
        }
    }

    /**
     * 统一推进某一个调度计划依赖图
     * @param addBatch 推进的批次数
     * @param taskDependencyId 调度计划依赖图id
     */
    public void advanceTaskDependency(Integer addBatch, Long taskDependencyId) {
        TaskDependency taskDependency = taskDependencyRepository.findById(taskDependencyId).orElseThrow(() -> new AppException("未找到taskDependencyId[%s]调度计划依赖", taskDependencyId));
        if (taskDependency.getEdges().isEmpty()) {
            throw new AppException("当前调度计划依赖图{%s}未配置任何调度计划",taskDependency.getName());
        }
        List<String> sourceTaskName = taskDependency.getEdges().stream().map(TaskDependencyEdge::getSource).collect(Collectors.toList());
        List<String> targetTaskName = taskDependency.getEdges().stream().map(TaskDependencyEdge::getTarget).collect(Collectors.toList());
        Set<String> jobIds = Stream.of(sourceTaskName, targetTaskName).collect(Collector.of(
                ArrayList<String>::new,
                List::addAll,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                }
        )).stream()
                .distinct()
                .map(taskName -> {
                    Job latestJob = jobService.getLatestJobByTaskName(taskName);
                    if (latestJob != null) {
                        return latestJob.getId();
                    } else {
                        return "";
                    }
                })
                .filter(taskName -> !taskName.equals(""))
                .collect(Collectors.toSet());

        updateBatchAndExec(addBatch, jobIds);
    }

    public void advanceJob(Integer addBatch, String jobId) {
        updateBatchAndExec(addBatch, Sets.newHashSet(jobId));
    }

    //==============================================  inner call

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

    /**
     * execute job.if the job have been limited by maxBatch,we need to pause this job until the maxBatch change(increment)
     *
     * @param job
     */
    private synchronized void executeJob(Job job) {
//        // judge the max_batch
//        boolean canExec = jobCanExecByMaxBatch(job);
//        if (!canExec) {
//            LOGGER.info("job[{}],maxBatch[{}]由于最大执行批次被限制，进入暂停状态", job.getId(), job.getTask().getMaxBatch());
//            pause(job.getId());
//        }
//        executeNodeJob(findStartNodeJob(job.getId()));
//        if (!canExec) {
//            flushJobStateAndHandleTriggerState(job);
//        }
        executeNodeJob(findStartNodeJob(job.getId()));
    }

    private synchronized void executeNodeJob(NodeJob nodeJob) {
        NodeTask nodeTask = Objects.requireNonNull(nodeJob.getNodeTask(), "未找到任务");
        if (!nodeJob.getState().isPaused()) {
            // the nodeJob'state will be none.this condition will be created by redo operation.
            if (!nodeJob.getState().equals(JobState.NONE)) {
                LOGGER.info("nodeJob[{}]不是初始化状态:state{} ", nodeJob.getId(), nodeJob.getState());
                return;
            }
            if (nodeJobWaitQueue.contains(nodeJob.getId())) {
                LOGGER.info("nodeJob[{}]被多次触发,可能是redo成功或跳过的节点导致", nodeJob.getId());
                return;
            }
            if (nodeTask.getTaskId().equalsIgnoreCase(NodeTask.CONTROL) || nodeTask.getTaskId().equalsIgnoreCase(NodeTask.START)) {
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
                if (canDispatch()) {
                    // concurrent control
                    modifyJobState(nodeJob, JobState.ACCEPTED);
                    logger.log(nodeJob.getId(), "节点任务准备派发，taskId[{}]，nodeJobId[{}]，state[{}]"
                            , nodeJob.getNodeTask().getTaskId(), nodeJob.getId(), nodeJob.getState());
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
            logger.log(nodeJob.getId(), "节点任务执行完毕，批次时间[{}]，taskId[{}]，nodeJobId[{}]，state[{}]",
                    parent.getShouldFireTime().format(formatter), nodeJob.getNodeTask().getTaskId(), nodeJob.getId(), nodeJob.getState());
            runNextJob(parent, nodeJob.getNodeId());
        }
    }

    private void modifyJobState(AbstractJob job, JobState state) {
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
    private synchronized void notifyWaitNodeJob() {
        int needReleaseJobs = maxRunningJobs - nodeJobRepository.countNodeJobsByRunningOrAcceptState();
        if (needReleaseJobs > 0) {
            // release waiting job
            for (int i = 0; i < Math.min(needReleaseJobs, nodeJobWaitQueue.size()); i++) {
                if (!nodeJobWaitQueue.isEmpty()) {
                    try {
                        String nodeJobId = nodeJobWaitQueue.take();
                        Optional<NodeJob> nodeJobInWaitQueue = nodeJobRepository.findById(nodeJobId); // this job'state could be illegal;  need validate
                        if (nodeJobInWaitQueue.isPresent()) {
                            LOGGER.info("任务出队：nodeJob[{}]", nodeJobId);
                            if (nodeJobInWaitQueue.get().getState().isNone() || nodeJobInWaitQueue.get().getState().isPaused()) {
                                // this operation will lose one callback of message.so we adopt i-- to offset this operation and need twice check by nodeJobWaitQueue.isEmpty()
                                if (nodeJobInWaitQueue.get().getState().isPaused()) {
                                    i--;
                                }
                                executeNodeJob(nodeJobInWaitQueue.get());
                            } else {
                                LOGGER.info("任务状态异常：nodeJob[{}]", nodeJobInWaitQueue.get().getId());
                                i--;
                            }
                        } else {
                            LOGGER.info("任务已被删除：nodeJob[{}]", nodeJobId);
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
        if (!nodeJobWaitQueue.contains(nodeJob.getId())) { // redo success job will cause the same nodeJobId was putted into nodeJobWaitQueue
            try {
                LOGGER.info("达到最大并发数，任务入队：nodeJob[{}]", nodeJob.getId());
                nodeJobWaitQueue.put(nodeJob.getId());
            } catch (InterruptedException e) {
                LOGGER.error("NodeJob等待队列入队异常:nodeJob[{}]", nodeJob.getId());
                e.printStackTrace();
            }
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
     * 3.running:when there exist a nodeJob is running ,the job'state is running.
     * 4.paused:when there isn't a nodeJob is running and exist one and more nodeJob is paused,then the job'state is paused
     * <p>
     * attention:the caller of this method must after modifyJobState or onJobFinished or runNextJob.
     *
     * @param nodeJob the nodeJob
     */
    private synchronized void flushParentStateAndHandleTriggerState(NodeJob nodeJob) {
        flushJobStateAndHandleTriggerState(getParentByNodeJob(nodeJob));
    }

    private synchronized void flushJobStateAndHandleTriggerState(Job job) {
        // indicate whether the job is the latest job
        // if the job is the latest job.we should sync trigger's state.
        Job latestJob = jobService.getLatestJobByTaskName(job.getTaskName());
        boolean isLatestJob = latestJob != null && latestJob.getId().equals(job.getId());
        // flush job state
        List<NodeJob> subJobs = job.getSubJobs();
        if (subJobs.isEmpty()) {
            modifyJobState(job, JobState.NONE);
            return;
        }

        // when there exist one running job or exist one nodejob in nodejobWaitQueue,the job'state is running or paused
        // attention:state of the job may be running or paused.if the state is paused.need modifyJobState and validate job'state before this method.
//        boolean inNodeJobWaitQueue = false;
//        for (NodeJob nodeJob : subJobs) {
//            if (!nodeJob.getState().isComplete() && nodeJobWaitQueue.contains(nodeJob.getId())) {
//                inNodeJobWaitQueue = true;
//                break;
//            }
//        }
//        if (inNodeJobWaitQueue || subJobs.stream().anyMatch(nd -> nd.getState().isRunning())) {
        if (subJobs.stream().anyMatch(nd -> nd.getState().isRunning())) {
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

        if (subJobs.stream().anyMatch(nd -> nd.getState().isPaused())) {
            modifyJobState(job, JobState.PAUSED);
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
        HashSet<String> nodeJobsInPausedMap = nodeJobPausedMap.getOrDefault(nodeJob.getContainer(), Sets.newHashSet());
        if (!nodeJobsInPausedMap.contains(nodeJob.getId())) { // redo success job will cause the same nodeJobId was added into nodeJobPausedMap
            LOGGER.info("job[{}]暂停,nodeTask[{}],nodeJob[{}]进入暂停等待区", nodeJob.getContainer(), nodeJob.getNodeTask().getTaskName(), nodeJob.getId());
            nodeJobsInPausedMap.add(nodeJob.getId());
            nodeJobPausedMap.put(nodeJob.getContainer(), nodeJobsInPausedMap);
        }
    }

    private TriggerKey getTriggerKeyByNodeJob(NodeJob nodeJob) {
        return jobRepository.findById(nodeJob.getContainer())
                .orElseThrow(() -> new AppException("未发现jobId[%s]的实例任务", nodeJob.getContainer()))
                .getTask().getTriggerKey();
    }

    private void resumePausedNodeJobsByJob(Job job) {
        job.getSubJobs().forEach(nd -> {
            if (nd.getState().equals(JobState.PAUSED)) {
                modifyJobState(nd, JobState.NONE);
            }
        });

        nodeJobPausedMap.getOrDefault(job.getId(), Sets.newHashSet())
                .forEach(nodeJobId -> nodeJobRepository.findById(nodeJobId).ifPresent(nd -> {
                    LOGGER.info("nodeJob[{}]暂停等待中恢复", nd.getId());
                    executeNodeJob(nd);
                }));
        nodeJobPausedMap.remove(job.getId());
    }

    private void resumePausedNodeJob(NodeJob nodeJob) {
        if (nodeJob.getState().isPaused()) {
            modifyJobState(nodeJob, JobState.NONE);
        }
        HashSet<String> nodeJobPausedIds = nodeJobPausedMap.getOrDefault(nodeJob.getContainer(), Sets.newHashSet());
        if (nodeJobPausedIds.contains(nodeJob.getId())) {
            nodeJobPausedIds.remove(nodeJob.getId());
            LOGGER.info("nodeJob[{}]暂停等待中恢复", nodeJob.getId());
            executeNodeJob(nodeJob);
        }
    }

    /**
     * judge the job whether can execute because of the dependencies between tasks
     * the condition is that we need all jobs of task depended by the task of this job and those job have the same batchTime.
     *
     * @param job
     * @return
     */
    private boolean jobCanExecByTaskDependency(Job job) {
        for (TaskDependencyEdge td : taskDependencyEdgeRepository.findAllByTarget(job.getTaskName())) {
            switch (td.getPrinciple()) {
                // at present,we only support the dependencies of tasks whose schedule type is monthly.
                case MONTHLY_2_MONTHLY:
                    Optional<Job> jobDepended = jobRepository.findByTaskNameAndBatchTime(td.getSource(), job.getBatchTime());
                    if (!jobDepended.isPresent() || !jobDepended.get().getState().isSuccess()) {
                        return false;
                    }
                    break;
                case DAYLY_2_MONTHLY:
                case MONTHLY_2_DAYLY:
                case DAYLY_2_DAYLY:
                default:
                    return false;
            }
        }
        return true;
    }

    /**
     * when
     *
     * @param addBatch the batch need to advance
     * @param jobIds   two condition:1. the special job   2.by start task dependency,all job whose state is none by check of the dependency
     */
    private void updateBatchAndExec(Integer addBatch, Set<String> jobIds) {
        List<String> taskNames = jobRepository.findAllByIdIn(Lists.newArrayList(jobIds)).stream().map(Job::getTaskName).distinct().collect(Collectors.toList());
        taskRepository.findAllByTaskNameIn(taskNames).forEach(tk -> {
            if (tk.getMaxBatch() != null) {
                tk.setMaxBatch(tk.getMaxBatch() + addBatch);
                taskRepository.save(tk);
            }
        });
        notifyWaitJobs(jobIds);
    }

    /**
     * when a job success or skip ;we need to call this method to notify job waited.
     */
    private void notifyJobInWaitSet() {
        notifyWaitJobs(jobWaitSet);
    }

    // notify those jobs which can run below task's dependencies and maxBatch.
    private void notifyWaitJobs(Set<String> jobIds) {
        if (jobIds != null) {
            jobIds.forEach(jobId -> {
                Optional<Job> jobWaited = jobRepository.findById(jobId);
                if (jobWaited.isPresent()) {
                    if (jobCanExecByTaskDependency(jobWaited.get()) && jobCanExecByMaxBatch(jobWaited.get())) {
                        jobWaitSet.remove(jobId);
                        LOGGER.info("job[{}],batchTime[{}]前置实例已全部完成,且未达到最大批次,准备执行", jobId, jobWaited.get().getBatchTime().toString());
                        executeJob(jobWaited.get());
                    }
                } else {
                    jobWaitSet.remove(jobId);
                }
            });
        }
    }

    /**
     * judge the job whether can execute because of task's maxBatch
     * if maxBatch is null,there isn't limit to running of job.
     * if the job which sequence with shouldFireTime is over the maxBatch ,the job can't execute.
     *
     * @param job
     * @return true:the job can run
     */
    private boolean jobCanExecByMaxBatch(Job job) {
        Integer maxBatch = job.getTask().getMaxBatch();
        return maxBatch == null ||
                jobRepository.findAllByTaskName(job.getTaskName())
                        .stream()
                        .sorted((o1, o2) -> o1.getShouldFireTime().isAfter(o2.getShouldFireTime()) ? 1 : -1)
                        .map(Job::getId)
                        .collect(Collectors.toList())
                        .indexOf(job.getId()) < maxBatch;
    }

}
