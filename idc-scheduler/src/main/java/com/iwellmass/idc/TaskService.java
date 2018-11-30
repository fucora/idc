package com.iwellmass.idc;

import java.util.List;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;

public interface TaskService {

	void saveTask(Task task);
	
	Task getTask(TaskKey taskKey);
	
	List<Task> getTasks(List<TaskKey> taskKey);

}
