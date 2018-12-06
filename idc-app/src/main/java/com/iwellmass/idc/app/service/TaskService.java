package com.iwellmass.idc.app.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.mapper.TaskMapper;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.app.vo.TaskQueryVO;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

@Service
public class TaskService {

	@Inject
	TaskRepository taskRepository;

	@Inject
	TaskMapper taskMapper;
	
	public void saveTask(Task task) {
		Task oldTask = taskRepository.findOne(task.getTaskKey());
		oldTask.setTaskName(task.getTaskName());
		oldTask.setDescription(task.getDescription());
		oldTask.setUpdatetime(LocalDateTime.now());
		taskRepository.save(task);
	}
	
	public Task getTask(TaskKey taskKey) {
		return taskRepository.findOne(taskKey);
	}

	public List<Task> getTasksByType(TaskType taskType) {
		return taskRepository.findByTaskType(taskType);
	}

	public PageData<Task> queryTask(TaskQueryVO taskQuery, Pager pager) {
		
		PageRequest pageable = new PageRequest(pager.getPage(), pager.getLimit(), Direction.DESC, "updatetime");
		
		Specification<Task> spec = taskQuery == null ? null : SpecificationBuilder.toSpecification(taskQuery);
		
		Page<Task> ret = taskRepository.findAll(spec, pageable);
		
		PageData<Task> task = new PageData<>((int)ret.getTotalElements(), ret.getContent());
		return task;
	}
}
