package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.QueryUtils;
import com.iwellmass.idc.app.vo.Assignee;
import com.iwellmass.idc.app.vo.CronTaskVO;
import com.iwellmass.idc.app.vo.ManualTaskVO;
import com.iwellmass.idc.app.vo.TaskQueryParam;
import com.iwellmass.idc.app.vo.TaskRuntimeVO;
import com.iwellmass.idc.app.vo.TaskVO;
import com.iwellmass.idc.scheduler.model.ScheduleType;
import com.iwellmass.idc.scheduler.model.Task;
import com.iwellmass.idc.scheduler.repository.TaskRepository;

@Service
public class TaskService {
	
	@Resource
	TaskRepository taskRepository;
	
	public TaskVO getTask(String name) {
		
		Task job = taskRepository.findById(name).orElseThrow(()-> new AppException("任务不存在"));

		TaskVO vo;
		if (job.getScheduleType() == ScheduleType.AUTO) {
			vo = new CronTaskVO();
		} else {
			vo = new ManualTaskVO();
		}
		BeanUtils.copyProperties(job, vo, "workflow");
		if (job.getStarttime() != null) {
			vo.setStartDate(job.getStarttime().toLocalDate());
		}
		if (job.getEndtime() != null) {
			vo.setEndDate(job.getEndtime().toLocalDate());
		}
		return vo;
	}

	public PageData<TaskRuntimeVO> query(TaskQueryParam jqm) {
		return QueryUtils.doJpaQuery(jqm, (p) -> {
			Specification<Task> spec = SpecificationBuilder.toSpecification(jqm);
			return taskRepository.findAll(spec, p).map(t -> {
				TaskRuntimeVO vo = new TaskRuntimeVO();
				BeanUtils.copyProperties(t, vo);
				return vo;
			});
		});
	}

	public List<Assignee> getAllAssignee() {
		return taskRepository.findAllAssignee().stream().map(Assignee::new).collect(Collectors.toList());
	}
}