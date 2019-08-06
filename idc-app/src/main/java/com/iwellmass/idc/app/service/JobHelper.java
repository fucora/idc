package com.iwellmass.idc.app.service;

import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.app.scheduler.ExecuteRequest;
import com.iwellmass.idc.app.scheduler.JobEnvAdapter;
import com.iwellmass.idc.message.FinishMessage;
import com.iwellmass.idc.scheduler.IDCJobExecutors;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.service.IDCLogger;
import lombok.Setter;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@Component
public class JobHelper {

    @Setter
    private Scheduler scheduler;

    @Autowired
    private IDCLogger idcLogger;

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


    public void startJob(AbstractJob job) {
        if (job.getTaskType() == TaskType.WORKFLOW) {
            executeJob((Job) job);
        } else {
            executeNodeJob((NodeJob) job);
        }
    }

    public void executeJob(Job job) {
        idcLogger.log(job.getId(), "执行workflow id={}", job.getTask().getWorkflow().getId());
        runNextJob(job, NodeTask.START);
    }

    public void executeNodeJob(NodeJob job) {
        NodeTask task = (NodeTask) Objects.requireNonNull(job.getTask(), "未找到任务");
        if (job.getNodeId().equals(NodeTask.END)) {
            idcLogger.log(job.getId(), "执行task end,container={}", job.getContainer());
            job.setState(JobState.FINISHED);
            FinishMessage message = FinishMessage.newMessage(job.getContainer());
            message.setMessage("执行结束");
            TaskEventPlugin.eventService(scheduler).send(message);
            return;
        }
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


    public void runNextJob(Job job, String startNode) {
        AbstractTask task = Objects.requireNonNull(job.getTask(), "未找到任务");
        Workflow workflow = Objects.requireNonNull(task.getWorkflow(), "未找到工作流");
        // 找到立即节点
        Set<String> successors = workflow.successors(startNode);
        Iterator<NodeJob> iterator = job.getSubJobs().stream()
                .filter(sub -> successors.contains(sub.getNodeId()))
                .iterator();

        // any success
        boolean anySuccess = false;
        while (iterator.hasNext()) {
            NodeJob next = iterator.next();
            try {
                Set<String> previous = workflow.getPrevious(next.getNodeId());

                //如果存在未完成的任务 则不继续执行
                boolean unfinishJob = job.getSubJobs().stream()
                        .filter(sub -> previous.contains(sub.getNodeId()))
                        .anyMatch(sub -> !sub.getState().isSuccess());
                if (unfinishJob) {
                    anySuccess = true;
                    continue;
                }
                startJob(next);
                anySuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                anySuccess |= false;
                next.setState(JobState.FAILED);
            }
        }
        // 贪婪模式
        if (!anySuccess) {
            job.setState(JobState.FAILED);
        }
    }
}
