package com.iwellmass.idc.app.scheduler;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.app.service.ExecParamHelper;
import com.iwellmass.idc.app.service.TaskService;
import com.iwellmass.idc.scheduler.service.IDCLogger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.app.service.JobService;
import com.iwellmass.idc.message.StartMessage;
import com.iwellmass.idc.scheduler.quartz.SuspendScheduleAfterExecution;

import lombok.Setter;

import java.util.List;

@DisallowConcurrentExecution
@SuspendScheduleAfterExecution
public class JobBootstrap implements org.quartz.Job {

    public static final String PROP_TASK_NAME = "taskName";

    static final Logger LOGGER = LoggerFactory.getLogger(JobBootstrap.class);

    @Setter
    JobService jobService;

    @Setter
    private String taskName;

    @Setter
    ExecParamHelper execParamHelper;

    @Setter
    TaskService taskService;

    @Setter
    IDCLogger logger;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 全局唯一
        String jobId = context.getFireInstanceId();
        try {
            LOGGER.info("开始执行任务：{} , taskName: {} ", jobId, taskName);
            // 恢复的任务，清理现场
            if (context.isRecovering()) {
                jobService.clear(jobId);
            }
            List<ExecParam> execParams = execParamHelper.parse(jobService.getTask(taskName));
            jobService.createJob(jobId, taskName, execParams);

            StartMessage message = StartMessage.newMessage(jobId);
            message.setMessage("启动任务");
            TaskEventPlugin.eventService(context.getScheduler()).send(message);
            logger.log(jobId, "创建任务实例,taskName[{}]，jobId[{}]，loadDate[{}]", taskName, jobId, ExecParamHelper.getLoadDate(execParams));
        } catch (Exception e) {
            logger.log(jobId, e.getMessage(), e);
        }
    }
}