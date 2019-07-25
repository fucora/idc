package com.iwellmass.idc.app.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContextException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.QueryUtils;
import com.iwellmass.idc.app.vo.Assignee;
import com.iwellmass.idc.app.vo.JobQueryParam;
import com.iwellmass.idc.app.vo.JobRuntimeVO;
import com.iwellmass.idc.app.vo.JobVO;
import com.iwellmass.idc.scheduler.model.AbstractJob;
import com.iwellmass.idc.scheduler.model.Job;
import com.iwellmass.idc.scheduler.model.Task;
import com.iwellmass.idc.scheduler.model.TaskID;
import com.iwellmass.idc.scheduler.repository.AllJobRepository;
import com.iwellmass.idc.scheduler.repository.JobRepository;
import com.iwellmass.idc.scheduler.repository.TaskRepository;

/**
 * Job 服务
 */
@Service
public class JobService {
	
	static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);
	
	@Resource
	TaskRepository taskRepository;

	@Resource
	JobRepository jobRepository;
	
	@Resource
	AllJobRepository allJobRepository;

	Job getJob(String id) {
		return jobRepository.findById(id).orElseThrow(()-> new AppException("任务 '" + id + "' 不存在"));
	}
	
	public Task getTask(String taskName) {
		return taskRepository.findById(new TaskID(taskName)).orElseThrow(()-> new AppException("调度 '" + taskName + "' 不存在"));
	}
	
	public PageData<JobRuntimeVO> query(JobQueryParam jqm) {
		Specification<Job> spec = SpecificationBuilder.toSpecification(jqm);
		return QueryUtils.doJpaQuery(jqm, pageable -> {
			return jobRepository.findAll(spec, pageable).map(job -> {
				JobRuntimeVO vo = new JobRuntimeVO();
				BeanUtils.copyProperties(job, vo);
				return vo;
			});
		});
	}
	
	public List<Assignee> getAllAssignee() {
		return jobRepository.findAllAssignee().stream().map(Assignee::new).collect(Collectors.toList());
	}
	
	public JobVO get(String id) {
		JobVO jobVO = new JobVO();
		Job job = getJob(id);
		BeanUtils.copyProperties(job, jobVO);
		return jobVO;
	}

	public void clear(String id) {
		// TODO
	}

	@Transactional
	public void createJob(String id, String taskName) {
		Task task = getTask(taskName);
		// 有可能前台强制取消了调度
		// 或者调度已过期、已被删除
//		if (task.getState().isTerminated()) {
//			LOGGER.error("调度已关闭：" + task.getState());
//		} else {
			Job job = new Job(id, task);
			jobRepository.save(job);
//		}
	}

	@Transactional
	public void test(String id, String action) {
		AbstractJob job = allJobRepository.findById(id).get();
		Method method = ReflectionUtils.findMethod(job.getClass(), action);
		try {
			method.invoke(job);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ApplicationContextException(e.getMessage(), e);
		}
	}

}
