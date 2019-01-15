package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.mapper.JobRuntimeMapper;
import com.iwellmass.idc.app.mapper.MapperUtil;
import com.iwellmass.idc.app.repo.JobRepository;
import com.iwellmass.idc.app.vo.Assignee;
import com.iwellmass.idc.app.vo.JobQuery;
import com.iwellmass.idc.app.vo.JobRuntime;
import com.iwellmass.idc.app.vo.JobRuntimeListVO;
import com.iwellmass.idc.app.vo.PauseRequest;
import com.iwellmass.idc.app.vo.ScheduleProperties;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.quartz.IDCPlugin;

@Service
public class JobService {

	static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

	@Inject
	private JobRepository jobRepository;
	
	@Inject
	private TaskService taskService;

	@Inject
	private JobRuntimeMapper jobRuntimeMapper;
	
	@Inject
	private IDCPlugin idcPlugin;
	
	// 获取计划
	public Job getJob(JobKey jobKey) {
		return jobRepository.findOne(jobKey);
	}

	// 调度运行时信息
	public JobRuntime getJobRuntime(JobKey jobKey) {
		return  jobRuntimeMapper.selectJobRuntime(jobKey);
	}
	
	// 动态查询调度计划列表
	public PageData<JobRuntimeListVO> getJobRuntime(JobQuery jobQuery, Pager pager) {
		return MapperUtil.doQuery(pager, ()->jobRuntimeMapper.selectJobRuntimeList(jobQuery));
	}

	// 所有责任人
	public List<Assignee> getAllAssignee() {
		return jobRepository.findAllAssignee().stream().map(id -> {
			Assignee asg = new Assignee();
			asg.setAssignee(id);
			return asg;
		}).collect(Collectors.toList());
	}

	
	// ~~ 调度相关 ~~
	public void schedule(ScheduleProperties sp) {
		try {
			taskService.validate(sp.getTaskKey());
			idcPlugin.schedule(sp.toJob());
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	public void reschedule(JobKey jobKey, ScheduleProperties sp) {
		try {
			Job job = null;
			if(sp != null) {
				job = sp.toJob();
			}
			idcPlugin.reschedule(jobKey, job);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	public void unschedule(JobKey jobKey) {
		try {
			idcPlugin.unschedule(jobKey);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	public void pause(PauseRequest request) {
		try {
			idcPlugin.pause(request);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	public void resume(JobKey jobKey) {
		try {
			idcPlugin.resume(jobKey);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}
}
