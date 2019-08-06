package com.iwellmass.idc.scheduler.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.iwellmass.idc.scheduler.repository.JobRepository;
import com.iwellmass.idc.scheduler.repository.NodeJobRepository;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.app.scheduler.JobBootstrap;
import com.iwellmass.idc.app.vo.task.ReTaskVO;
import com.iwellmass.idc.app.vo.task.TaskVO;
import com.iwellmass.idc.scheduler.repository.TaskRepository;

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
        }
        // 创建作业
        JobDetail jobDetail = JobBuilder.newJob(JobBootstrap.class)
                .withIdentity(task.getTaskName(), task.getTaskGroup())
                .requestRecovery().build();

        // 调度
        try {
            taskRepository.save(task);
            Trigger trigger = vo.buildTrigger(task.getTriggerKey());

            trigger.getJobDataMap().put(JobBootstrap.PROP_TASK_NAME, task.getTaskName());
            Date d = qs.scheduleJob(jobDetail, trigger);
            LOGGER.info("task scheduled :" + d);
        } catch (SchedulerException e) {
            throw new AppException(e);
        }
    }

    @Transactional
    public void reschedule(ReTaskVO reVO) {
        Task task = getTask(reVO.getTaskName());
        // 清理现场
        clear(task);
        BeanUtils.copyProperties(reVO, task);
        try {
            Trigger trigger = reVO.buildTrigger(task.getTriggerKey());
            qs.rescheduleJob(trigger.getKey(), trigger);
            taskRepository.save(task);
        } catch (SchedulerException e) {
            throw new AppException(e);
        }
    }

    @Transactional
    public void unschedule(String name) {
        Task task = getTask(name);
        try {
            qs.unscheduleJob(task.getTriggerKey());
        } catch (SchedulerException e) {
            throw new AppException(e);
        }
//        taskRepository.delete(task);    // 取消调度时不删除task信息
        task.setState(TaskState.CANCEL);
        taskRepository.save(task);

    }

    @Transactional
    public void pause(String name) {
        Task job = getTask(name);
        try {
            qs.pauseTrigger(job.getTriggerKey());
        } catch (SchedulerException e) {
            throw new AppException(e);
        }
    }

    @Transactional
    public void resume(String name) {
        Task job = getTask(name);
        // TODO check
        try {
            qs.resumeTrigger(job.getTriggerKey());
        } catch (SchedulerException e) {
            throw new AppException(e);
        }
    }


    // todo 清除之前任务产生job,node_job实例，甚至log信息
    public void clear(Task task) {
        List<Job> jobs = jobRepository.findAllByTaskName(task.getTaskName());
        // node_job
        nodeJobRepository.deleteAllByContainerIn(jobs.stream().map(Job::getId).collect(Collectors.toList()));
        // job
        jobRepository.deleteAll(jobs);
    }
}
