package com.iwellmass.idc;

import java.util.List;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

public interface TaskService {

	void saveTask(Task task);
	
	Task getTask(TaskKey taskKey);
	
	List<Task> getTasks(List<TaskKey> taskKey);

	List<Task> getTasksByType(TaskType workflowSubTask);

}
