package com.iwellmass.idc.app;

import com.alibaba.fastjson.JSON;
import com.iwellmass.idc.scheduler.model.*;
import org.assertj.core.util.Lists;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

/**
 * ${DESC}
 *
 * @author hawkins
 * @date 2019-07-25 17:02
 */
public class IDCTestUtils {

    public static Workflow loadWorkflow(String taskId) {
        try {
            String file = IDCSchedulerTest.class.getClassLoader().getResource("workflow-test.json").getFile();
            RandomAccessFile accessFile = new RandomAccessFile(file, "r");

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = accessFile.readLine()) != null) {
                sb.append(new String(line.getBytes(StandardCharsets.ISO_8859_1), "UTF-8"));
            }

            String workflowStr = sb.toString();
            System.out.println(workflowStr);

            Workflow workflow = JSON.parseObject(workflowStr, Workflow.class);
            workflow.setId(taskId);
            return workflow;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Job createJob(Task task, List<NodeTask> taskNodes, String jobId) {
        Job job = new Job();
        job.setId(jobId);
        job.setTaskName(task.getTaskName());
        job.setState(JobState.NONE);
        job.setTask(task);
        job.setTaskType(TaskType.WORKFLOW);


        List<NodeJob> subJobs = Lists.newArrayList();
        for (NodeTask nt: taskNodes) {

            NodeJob nodeJob = new NodeJob();
            nodeJob.setId("");
            nodeJob.setNodeId(nt.getId());
            nodeJob.setState(JobState.NONE); // 默认状态为NONE
            nodeJob.setTaskType(TaskType.SIMPLE); // 子任务类型为SIMPLE
            nodeJob.setNodeTask(nt); // 关联到NodeTask

            subJobs.add(nodeJob);
        }
        job.setSubJobs(subJobs);
        return job;
    }

}
