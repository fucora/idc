package com.iwellmass.idc.scheduler.model;

import com.alibaba.fastjson.JSON;
import com.iwellmass.idc.app.IDCTestUtils;
import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.app.service.JobService;
import com.iwellmass.idc.app.vo.task.CronTaskVO;
import com.iwellmass.idc.model.CronType;
import com.iwellmass.idc.scheduler.IDCJobExecutors;
import com.iwellmass.idc.scheduler.quartz.IDCJobStore;
import com.iwellmass.idc.scheduler.quartz.IDCSchedulerFactory;
import com.iwellmass.idc.scheduler.repository.AllJobRepository;
import com.iwellmass.idc.scheduler.repository.TaskRepository;
import com.iwellmass.idc.scheduler.service.IDCJobExecutor;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.*;
import org.quartz.simpl.RAMJobStore;
import org.quartz.spi.JobStore;
import org.springframework.beans.BeanUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.iwellmass.idc.app.IDCTestUtils.createJob;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


/**
 * 调度器测试
 *
 * @author hawkins
 * @date 2019-07-23 16:45
 */
public class IDCSchedulerTest {

    IDCScheduler idcScheduler;

    Scheduler scheduler;

    @Mock
    TaskRepository taskRepository;

    @Mock
    JobService jobService;

    @Mock
    AllJobRepository allJobRepository;

    @Mock
    IDCJobExecutor idcJobExecutor;

    @Mock
    IDCJobStore idcJobStore;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        TaskEventPlugin taskEventPlugin = new TaskEventPlugin();
        taskEventPlugin.setJobService(jobService);
        taskEventPlugin.setAllJobRepository(allJobRepository);
        taskEventPlugin.setIdcJobStore(idcJobStore);

        JobStore js = new RAMJobStore();
        scheduler = IDCSchedulerFactory.getScheduler(taskEventPlugin, js);

        idcScheduler = new IDCScheduler();
        idcScheduler.taskRepository = taskRepository;
        idcScheduler.qs = scheduler;

        IDCJobExecutors.setGlobalExecutor(idcJobExecutor); // 设置通用执行器
    }

    @Test
    public void schedule() throws IOException, InterruptedException, SchedulerException {
        CronTaskVO taskVO = new CronTaskVO();
        taskVO.setWorkflowId("testId");
        taskVO.setTaskName("testName");
        taskVO.setDomain("testDomain");
        taskVO.setCronType(CronType.CUSTOMER);
        taskVO.setExpression("0/5 * * * * ? *");

        mockTaskRepository(taskVO);

        idcScheduler.schedule(taskVO);
        idcScheduler.qs.start();

        Thread.sleep(1000 * 10);
    }


    private void mockTaskRepository(CronTaskVO taskVO) {
        Workflow workflow = IDCTestUtils.loadWorkflow(taskVO.getWorkflowId());

        Task task = new Task();
        BeanUtils.copyProperties(taskVO, task);
        task.setWorkflow(workflow);

        when(taskRepository.findById(new TaskID(taskVO.getTaskName()))).thenReturn(Optional.of(task));

        // 模拟jobService的创建job的行为
        doAnswer(invocation -> {
            String jobId = invocation.getArgument(0); // jobId
            String taskName = invocation.getArgument(1); // taskName
            System.out.println(jobId + " : " + taskName);

            Job job = createJob(task, workflow.getTaskNodes(), jobId);

            when(allJobRepository.findById(jobId)).thenReturn(Optional.of(job));

            return null;
        }).when(jobService).createJob(anyString(), anyString());

    }

}