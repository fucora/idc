package com.iwellmass.idc.quartz;

import java.util.List;

import com.iwellmass.idc.TaskService;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;

public class SimpleTaskService implements TaskService{

	@Override
	public Task getTask(TaskKey taskKey) {
		return null;
	}

	@Override
	public List<Task> getTasks(List<TaskKey> taskKey) {
		return null;
	}

}
