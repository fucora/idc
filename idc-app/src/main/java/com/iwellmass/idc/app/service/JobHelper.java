package com.iwellmass.idc.app.service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.app.scheduler.ExecuteRequest;
import com.iwellmass.idc.app.scheduler.JobEnvAdapter;
import com.iwellmass.idc.message.FinishMessage;
import com.iwellmass.idc.scheduler.IDCJobExecutors;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.quartz.IDCJobStore;
import com.iwellmass.idc.scheduler.quartz.ReleaseInstruction;
import com.iwellmass.idc.scheduler.repository.AllJobRepository;
import com.iwellmass.idc.scheduler.repository.JobRepository;
import com.iwellmass.idc.scheduler.repository.NodeJobRepository;
import com.iwellmass.idc.scheduler.repository.WorkflowRepository;
import com.iwellmass.idc.scheduler.service.IDCLogger;
import lombok.Setter;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
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

    //==============================================  processor call

    public void start(AbstractJob job) {
        if (job.getState().isComplete()) {
            throw new JobException("任务已执行");
        }
        if (job.getTask() == null) {
            throw new JobException("任务不存在");
        }
        if (job.getTaskType() == TaskType.WORKFLOW) {
            executeJob((Job) job);
        } else {
            executeNodeJob((NodeJob) job);
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
    }

    /**
     * redo job:adapt different strategy with the kind of job ,the kind of absJob contain nodeJob and job
     * when job is nodeJob: clear the nodeJob and generate a new nodeJob instance,the new nodeJob instance contain new jobId,but the containnerId must use oldJod's containnerId
     * when job is task's instance,job : the jobId can't be generated then clear all subJobs of the job and recreate all job's subJobs
     * @param job
     */
    public void redo(AbstractJob job) {
        if (job.getState().equals(JobState.FINISHED)) {
            throw new AppException("该job以完成");
        }
        if (job.getTaskType() == TaskType.WORKFLOW) {
            executeJob((Job) job);
        } else {
            // create new nodeJob instance
            NodeJob newNodeJob = new NodeJob(job.toNodeJob().getContainer(), job.toNodeJob().getNodeTask());
            // clear
            nodeJobRepository.delete(job.toNodeJob());
            nodeJobRepository.save(newNodeJob);
            executeNodeJob(newNodeJob);
        }
    }

    public void cancle(AbstractJob job) {
        checkRunning(job);

    }

    public void skip(AbstractJob job) {
        checkRunning(job);
        modifyJobState(job, JobState.SKIPPED);
        onJobFinished(job);
    }

    //==============================================  this class call

    private void checkRunning(AbstractJob job) {
        if (job.getState().isComplete()) {
            throw new JobException("任务已结束: " + job.getState());
        }
    }

    private void checkNotRunning(AbstractJob job) {
        if (!job.getState().isComplete()) {
            throw new JobException("任务已结束: " + job.getState());
        }
    }

    private void startJob(AbstractJob job) {
        if (job.getTaskType() == TaskType.WORKFLOW) {
            executeJob((Job) job);
        } else {
            executeNodeJob((NodeJob) job);
        }
    }

    private void executeJob(Job job) {
        idcLogger.log(job.getId(), "执行workflow id={}", job.getTask().getWorkflow().getId());
        modifyJobState(job, JobState.RUNNING);
        runNextJob(job, NodeTask.START);
    }

    private synchronized void executeNodeJob(NodeJob job) {
        NodeTask task = Objects.requireNonNull(job.getTask(), "未找到任务");
        if (job.getNodeId().equals(NodeTask.END)) {
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
        Workflow workflow = Objects.requireNonNull(task.getWorkflow(), "未找到工作流");
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
            Job job = (Job) runningJob;
            TriggerKey tk = job.getTask().getTriggerKey();
            if (job.getState().isComplete()) {
                if (job.getState().isSuccess()) {
                    idcJobStore.releaseTrigger(tk, ReleaseInstruction.RELEASE);
                } else {
                    idcJobStore.releaseTrigger(tk, ReleaseInstruction.SET_ERROR);
                }
            }
        } else {
            NodeJob nodeJob = (NodeJob) runningJob;
            Workflow workflow = workflowRepository.findById(nodeJob.getWorkflowId()).get();
            Job parent = (Job) allJobRepository.findById(nodeJob.getContainer()).get();
            parent.getTask().setWorkflow(workflow);
            runNextJob(parent, nodeJob.getNodeId());
        }
    }

    private void modifyJobState(AbstractJob job, JobState state) {
        job.setState(state);
        allJobRepository.save(job);
    }
}
