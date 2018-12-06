package com.iwellmass.idc;

import java.util.List;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;

public interface IDCPluginService {
	
	void saveTask(Task task);
	
	Task findTask(TaskKey taskKey);
	
	List<Task> findTasks(List<TaskKey> taskKey);

	public void saveJob(Job job);
	
	public Job findJob(JobKey jobKey);

}
