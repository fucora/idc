package com.iwellmass.idc;

import java.util.List;

import com.iwellmass.idc.model.*;

public interface IDCPluginService {
	
	void saveTask(Task task);
	
	Task findTask(TaskKey taskKey);
	
	List<Task> findTasks(List<TaskKey> taskKey);

	public void saveJob(Job job);
	
	public Job findJob(JobKey jobKey);

	JobInstance findByInstanceId(Integer instanceId);

}
