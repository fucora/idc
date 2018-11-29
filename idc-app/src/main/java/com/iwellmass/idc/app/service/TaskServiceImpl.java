package com.iwellmass.idc.app.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.iwellmass.idc.TaskService;
import com.iwellmass.idc.app.mapper.TaskMapper;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;

@Service
public class TaskServiceImpl implements TaskService{

	@Inject
	TaskRepository taskRepository;

	@Inject
	TaskMapper taskMapper;
	
	@Override
	public Task getTask(TaskKey taskKey) {
		return taskRepository.findOne(taskKey);
	}

	@Override
	public List<Task> getTasks(List<TaskKey> taskKeys) {
		return taskMapper.selectBatch(taskKeys);
	}
}
