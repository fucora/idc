package com.iwellmass.idc.app.message;

import com.iwellmass.idc.app.IDCTestUtils;
import com.iwellmass.idc.message.FinishMessage;
import com.iwellmass.idc.message.StartMessage;
import com.iwellmass.idc.scheduler.IDCJobExecutors;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.quartz.IDCJobStore;
import com.iwellmass.idc.scheduler.repository.AllJobRepository;
import com.iwellmass.idc.scheduler.repository.WorkflowRepository;
import com.iwellmass.idc.scheduler.service.IDCJobExecutor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Optional;

import static com.iwellmass.idc.app.IDCTestUtils.createJob;
import static org.mockito.Mockito.when;

/**
 * test TaskEventProcessor
 *
 * @author hawkins
 * @date 2019-07-25 16:44
 */
public class TaskEventProcessorTest {

    TaskEventProcessor taskEventProcessor;

    @Mock
    AllJobRepository allJobRepository;

    @Mock
    WorkflowRepository workflowRepository;

    @Mock
    IDCJobExecutor idcJobExecutor;

    @Mock
    IDCJobStore idcJobStore;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        taskEventProcessor = new TaskEventProcessor();
        taskEventProcessor.setAllJobRepository(allJobRepository);
        taskEventProcessor.setWorkflowRepository(workflowRepository);
        taskEventProcessor.setIdcJobStore(idcJobStore);

        IDCJobExecutors.setGlobalExecutor(idcJobExecutor); // 设置通用执行器
    }

    @Test
    public void execute() throws JobExecutionException {

        // 模拟工作流
        String workflowId001 = "testWorkflowId001";
        Workflow workflow = mockWorkflow(workflowId001);

        // 模拟一个调度计划
        Task task = mockTask(workflowId001);

        // 模拟一个触发的任务
        String jobId = "001";
        List<NodeTask> taskNodes = workflow.getTaskNodes();
        mockJob(task, taskNodes, jobId);

        // 模拟发送消息
        StartMessage message = StartMessage.newMessage(jobId);
        taskEventProcessor.setMessage(message);

        // 执行任务
        taskEventProcessor.execute(null);

        // 模拟结束任务
        FinishMessage finishMessage = FinishMessage.newMessage(jobId);
        taskEventProcessor.setMessage(finishMessage);
        taskEventProcessor.execute(null);
    }

    private Workflow mockWorkflow(String taskId) {

        Workflow workflow = IDCTestUtils.loadWorkflow(taskId);


        when(workflowRepository.findById(taskId)).thenReturn(Optional.of(workflow));

        return workflow;
    }

    private void mockJob(Task task, List<NodeTask> taskNodes, String jobId) {
        Job job = createJob(task, taskNodes, jobId);

        when(allJobRepository.findById(jobId)).thenReturn(Optional.of(job));
    }


    private  Task mockTask(String workflowId001) {
        String taskName = "testTask";
        String taskGroup = "testGroup";

        // 根据workflow生成job
        Task task = new Task();
        task.setTaskId(workflowId001);
        task.setTaskType(TaskType.WORKFLOW);
        task.setTaskName(taskName);
        task.setTaskGroup(taskGroup);

        return task;
    }

}