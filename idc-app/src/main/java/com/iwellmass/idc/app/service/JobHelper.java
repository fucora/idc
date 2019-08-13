package com.iwellmass.idc.app.service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.app.scheduler.ExecuteRequest;
import com.iwellmass.idc.app.scheduler.JobEnvAdapter;
import com.iwellmass.idc.message.FinishMessage;
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
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * @author nobita chen
 * @email nobita0522@qq.com
 * @date 2019/6/25 15:04
 */
@Component
public class JobHelper {

    @Setter
    private Scheduler scheduler;

    @Inject
    private IDCLogger idcLogger;
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

    public void success(AbstractJob job) {
        checkRunning(job);
        modifyJobState(job, JobState.FINISHED);
        onJobFinished(job);
    }

    public void failed(AbstractJob job) {
        checkRunning(job);
        modifyJobState(job, JobState.FAILED);
        try {
            if (scheduler.checkExists(job.asJob().getTask().getTriggerKey())) {
                idcJobStore.releaseTrigger(job.asJob().getTask().getTriggerKey(), ReleaseInstruction.SET_ERROR);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * redo job:adapt different strategy with the kind of job ,the kind of absJob contain nodeJob and job
     * when job is nodeJob: clear the nodeJob and generate a new nodeJob instance,the new nodeJob instance contain new jobId,but the containnerId must use oldJod's containnerId
     * when job is task's instance,job : the jobId can't be generated then clear all subJobs of the job and recreate all job's subJobs
     *
     * @param job
     */
    public void redo(AbstractJob job) {
        if (job.getState().isSuccess()) {
            throw new AppException("该job以完成:" + job.getId());
        }
        if (job.getTaskType() == TaskType.WORKFLOW) {
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
            jobService.createJob(job.getId(), job.asJob().getTask().getTaskName());
            executeJob(jobRepository.findById(job.getId()).get());
        } else {
            // create new nodeJob instance
            NodeJob newNodeJob = new NodeJob(job.asNodeJob().getContainer(), job.asNodeJob().getNodeTask());
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
                    subJob.setState(JobState.SKIPPED);
                }
            });
        }
        modifyJobState(job, JobState.SKIPPED);
        onJobFinished(job);
    }

    //==============================================  this class call

    private void checkRunning(AbstractJob job) {
        if (job.getState().isComplete()) {
            throw new JobException("job:" + job.getId() +  "已结束,状态为: " + job.getState());
        }
    }

    private void checkSuccess(AbstractJob job) {
        if (job.getState().isSuccess()) {
            throw new JobException("job:" + job.getId() +  "已成功或跳过,状态为: " + job.getState());
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
        idcLogger.log(job.getId(), "执行workflow id={}", job.getTask().getWorkflow().getId());
        modifyJobState(job, JobState.RUNNING);
        runNextJob(job, NodeTask.START);
    }

    private synchronized void executeNodeJob(NodeJob job) {
        NodeTask task = Objects.requireNonNull(job.getTask(), "未找到任务");
        if (job.getNodeId().equals(NodeTask.END) && !jobRepository.findById(job.getContainer()).get().getState().isComplete()) {
            idcLogger.log(job.getId(), "执行task end,container={}", job.getContainer());
            FinishMessage message = FinishMessage.newMessage(job.getContainer());
            message.setMessage("执行结束");
            TaskEventPlugin.eventService(scheduler).send(message);
            return;
        }
        if (job.getState().equals(JobState.NONE)) {
            modifyJobState(job, JobState.RUNNING);
            idcLogger.log(job.getId(), "执行task id={}, task = {},container={}", job.getId(), job.getTask().getTaskId(), job.getContainer());
            ExecuteRequest request = new ExecuteRequest();
            request.setDomain(task.getDomain());
            request.setContentType(task.getType());
            JobEnvAdapter jobEnvAdapter = new JobEnvAdapter();
            jobEnvAdapter.setTaskId(task.getTaskId());
            jobEnvAdapter.setInstanceId(job.getId());
            request.setJobEnvAdapter(jobEnvAdapter);
            IDCJobExecutors.getExecutor().execute(request);
        }
    }

    private void runNextJob(Job job, String startNode) {
        AbstractTask task = Objects.requireNonNull(job.getTask(), "未找到任务");
        Workflow workflow = workflowRepository.findById(task.getWorkflowId()).orElseThrow(() -> new AppException("未找到指定工作流"));
        task.setWorkflow(workflow);
        // 找到立即节点
        Set<String> successors = workflow.successors(startNode);
        Iterator<NodeJob> iterator = job.getSubJobs().stream()
                .filter(sub -> successors.contains(sub.getNodeId()))
                .iterator();

        while (iterator.hasNext()) {
            NodeJob next = iterator.next();
            try {
                Set<String> previous = workflow.getPrevious(next.getNodeId());

                //如果存在未完成的任务 则不继续执行
                boolean unfinishJob = job.getSubJobs().stream()
                        .filter(sub -> previous.contains(sub.getNodeId()))
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
        } else {
            NodeJob nodeJob = runningJob.asNodeJob();
            Workflow workflow = workflowRepository.findById(nodeJob.getWorkflowId()).get();
            Job parent = (Job) allJobRepository.findById(nodeJob.getContainer()).get();
            parent.getTask().setWorkflow(workflow);
            runNextJob(parent, nodeJob.getNodeId());
        }
    }

    private void modifyJobState(AbstractJob job, JobState state) {
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
