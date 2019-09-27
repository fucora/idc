package com.iwellmass.idc.scheduler.model;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.app.scheduler.JobBootstrap;
import com.iwellmass.idc.app.service.TaskService;
import com.iwellmass.idc.app.util.JsonUtils;
import com.iwellmass.idc.app.vo.TaskRuntimeVO;
import com.iwellmass.idc.app.vo.task.ReTaskVO;
import com.iwellmass.idc.app.vo.task.TaskVO;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.scheduler.repository.JobRepository;
import com.iwellmass.idc.scheduler.repository.NodeJobRepository;
import com.iwellmass.idc.scheduler.repository.TaskRepository;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 领域服务，负责调度相关的操作
 */
@Service
public class IDCScheduler {

    static final Logger LOGGER = LoggerFactory.getLogger(IDCScheduler.class);

    @Resource
    TaskRepository taskRepository;
    @Resource
    JobRepository jobRepository;
    @Resource
    NodeJobRepository nodeJobRepository;
    @Resource
    TaskService taskService;

    @Resource
    Scheduler qs;

    public Task getTask(String name) {
        return taskRepository.findById(new TaskID(name)).orElseThrow(() -> new AppException("未找到调度计划"));
    }

    @Transactional
    public void schedule(TaskVO vo) {
        if (taskRepository.findById(new TaskID(vo.getTaskName())).isPresent()) {
            throw new AppException("该调度计划已存在,若为新调度计划,请更改计划名称");
        }
        Task task = new Task(vo);
        BeanUtils.copyProperties(vo, task);
        if (vo.getScheduleType().equals(ScheduleType.AUTO)) {

            task.setStartDateTime(LocalDateTime.of(vo.getStartDate(), LocalTime.MIN));// 生效时间
            task.setEndDateTime(LocalDateTime.of(vo.getEndDate(), LocalTime.of(23, 59, 59)));    // 失效时间  // con't use  LocalTime.Max

            shceduleJob(vo, task);


        }

        taskRepository.save(task);

    }


    public void shceduleJob(TaskVO vo, Task task) {
        // 创建作业
        JobDetail jobDetail = JobBuilder.newJob(JobBootstrap.class)
                .withIdentity(task.getTaskName(), task.getTaskGroup())
                .requestRecovery().build();

        // 调度
        try {
            Trigger trigger = vo.buildTrigger(task.getTriggerKey());
            trigger.getJobDataMap().put(JobBootstrap.PROP_TASK_NAME, task.getTaskName());

            if(ScheduleType.MANUAL.equals(vo.getScheduleType()) && Objects.nonNull(vo.getParams())){


                trigger.getJobDataMap().put(JobBootstrap.PROP_TASK_PARAMS, JsonUtils.toJSon(vo.getParams()));
                task.setParams(vo.getParams());
                taskRepository.save(task);
            }

            Date d = qs.scheduleJob(jobDetail, trigger);
            LOGGER.info("task scheduled :" + d);
        } catch (Exception e) {
            throw new AppException(e);
        }
    }



    public void updateScheduleTask(Task task) {
        taskRepository.save(task);
    }


    // 正在执行的trigger 不能直接调用该接口
    @Transactional
    public void reschedule(ReTaskVO reVO) {
        Task oldTask = getTask(reVO.getTaskName());
        try {

            if (qs.checkExists(oldTask.getTriggerKey())) {
                throw new AppException("请先取消调度后再重新调度");
            }
            // 清理现场
            clear(oldTask);
            Task newTask = reVO.buildNewTask(oldTask);
            BeanUtils.copyProperties(reVO, newTask);
            if (reVO.getScheduleType().equals(ScheduleType.AUTO)) {
                newTask.setStartDateTime(LocalDateTime.of(reVO.getStartDate(), LocalTime.MIN));// 生效时间
                newTask.setEndDateTime(LocalDateTime.of(reVO.getEndDate(), LocalTime.of(23, 59, 59)));    // 失效时间  // con't use  LocalTime.Max
            }
            taskRepository.save(newTask);
            Trigger trigger = reVO.buildTrigger(newTask.getTriggerKey());
            trigger.getJobDataMap().put(JobBootstrap.PROP_TASK_NAME, oldTask.getTaskName());
            JobDetail jobDetail = JobBuilder.newJob(JobBootstrap.class)
                    .withIdentity(oldTask.getTaskName(), oldTask.getTaskGroup())
                    .requestRecovery().build();

            qs.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new AppException(e);
        }
    }

    @Transactional
    public void unschedule(String name) {
        Task task = getTask(name);
        // check this task whether contain running-job and we must judge it by nodeJob of task's job . if it contain then this task con't reschedule.
        if (nodeJobRepository.findAllByContainerIn(jobRepository.findAllByTaskName(name).stream().map(Job::getId).collect(Collectors.toList()))
                .stream().anyMatch(n -> n.getState()== JobState.RUNNING || n.getState()== JobState.ACCEPTED)) {
            throw new AppException("该调度计划存在正在执行的任务,请等待执行完成后再尝试");
        }

        try {
            qs.unscheduleJob(task.getTriggerKey());
        } catch (SchedulerException e) {
            throw new AppException(e);
        }
//        taskRepository.delete(task);    // don't delete task
        // delete all job and nodeJob
        List<Job> jobs = jobRepository.findAllByTaskName(name);
        List<NodeJob> nodeJobs = nodeJobRepository.findAllByContainerIn(jobs.stream().map(Job::getId).collect(Collectors.toList()));
        nodeJobRepository.deleteAll(nodeJobs);
        jobRepository.deleteAll(jobs);
//        taskRepository.save(task);

    }

    @Transactional
    public void pause(String name) {
        Task task = getTask(name);
        TaskRuntimeVO vo = new TaskRuntimeVO();
        BeanUtils.copyProperties(task, vo);
        taskService.twiceValidateState(task, vo);
        if (!vo.getState().isRunning()) {
            throw new AppException("该计划:" + task.getTaskName() + "未运行,状态为:" + vo.getState().name());
        }
        try {
            LOGGER.info("暂停任务:" + name);
            if (qs.checkExists(task.getTriggerKey())) {
                qs.pauseTrigger(task.getTriggerKey());
            }
        } catch (SchedulerException e) {
            throw new AppException(e);
        }
    }

    @Transactional
    public void resume(String name) {
        Task task = getTask(name);
        TaskRuntimeVO vo = new TaskRuntimeVO();
        BeanUtils.copyProperties(task, vo);
        taskService.twiceValidateState(task, vo);
        if (!vo.getState().isPaused()) {
            throw new AppException("该计划:" + task.getTaskName() + "未处于暂停状态,状态为:" + vo.getState().name());
        }
        try {
            LOGGER.info("恢复任务:" + name);
            qs.resumeTrigger(task.getTriggerKey());
        } catch (SchedulerException e) {
            throw new AppException(e);
        }
    }

    public void clear(Task task) {
        List<Job> jobs = jobRepository.findAllByTaskName(task.getTaskName());
        // node_job
        nodeJobRepository.deleteAllByContainerIn(jobs.stream().map(Job::getId).collect(Collectors.toList()));
        // job
        jobRepository.deleteAll(jobs);
        // log ...
    }
}
