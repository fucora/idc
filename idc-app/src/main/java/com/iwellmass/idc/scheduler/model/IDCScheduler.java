package com.iwellmass.idc.scheduler.model;

import javax.annotation.Resource;

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
import com.iwellmass.idc.app.scheduler.Taskinitializer;
import com.iwellmass.idc.app.vo.ReTaskVO;
import com.iwellmass.idc.app.vo.TaskVO;
import com.iwellmass.idc.scheduler.repository.TaskRepository;

@Service
public class IDCScheduler {

	static final Logger LOGGER = LoggerFactory.getLogger(IDCScheduler.class);

	@Resource
	TaskRepository taskRepository;

	@Resource
	Scheduler qs;

	public Task getJob(String name) {
		return taskRepository.findById(name).orElseThrow(() -> new AppException("未找到调度计划"));
	}

	@Transactional
	public void schedule(TaskVO vo) {
		Task task = new Task(vo.getTaskName(), vo.getTaskId(), vo.getTaskGroup());
		BeanUtils.copyProperties(vo, task);
		// 创建作业
		JobDetail jobDetail = JobBuilder.newJob(Taskinitializer.class)
			.withIdentity(task.getTaskId(), task.getDomain())
			.requestRecovery().build();
		
		// 调度
		try {
			Trigger trigger = vo.buildTrigger(task.getTaskName(), Task.GROUP_PRIMARY);
			qs.scheduleJob(jobDetail, trigger);
			taskRepository.save(task);
		} catch (SchedulerException e) {
			throw new AppException(e);
		}
	}

	@Transactional
	public void reschedule(String name, ReTaskVO reVO) {
		Task job = getJob(name);
		// 清理现场
		job.clear();
		BeanUtils.copyProperties(reVO, job);
		try {
			Trigger trigger = reVO.buildTrigger(job.getTaskName(), Task.GROUP_PRIMARY);
			qs.rescheduleJob(trigger.getKey(), trigger);
			taskRepository.save(job);
		} catch (SchedulerException e) {
			throw new AppException(e);
		}
	}

	@Transactional
	public void unschedule(String name) {
		Task job = getJob(name);
		try {
			qs.unscheduleJob(job.getTriggerKey());
		} catch (SchedulerException e) {
			throw new AppException(e);
		}
	}

	@Transactional
	public void pause(String name) {
		Task job = getJob(name);
		try {
			qs.pauseTrigger(job.getTriggerKey());
		} catch (SchedulerException e) {
			throw new AppException(e);
		}
	}

	@Transactional
	public void resume(String name) {
		Task job = getJob(name);
		// TODO check
		try {
			qs.resumeTrigger(job.getTriggerKey());
		} catch (SchedulerException e) {
			throw new AppException(e);
		}
	}
}
