package com.iwellmass.idc;

import java.util.List;

import com.iwellmass.idc.model.*;

public interface IDCPluginService {
	
	void saveTask(Task task);
	
	Task findTask(TaskKey taskKey);
	
	List<Task> findTasks(List<TaskKey> taskKey);

	void saveJob(Job job);
	
	Job findJob(JobKey jobKey);

	JobInstance findByInstanceId(Integer instanceId);

	void saveJobDependencies(List<JobDependency> jobDependencies);

	void clearJobDependencies(JobKey jobKey);

}
