package com.iwellmass.idc;

import java.util.List;

import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.TaskKey;

public interface DependencyService {
	
	public List<TaskKey> getSuccessors(TaskKey parentTaskKey, TaskKey taskKey);
	
	public List<TaskKey> getPredecessors(TaskKey parentTaskKey, TaskKey taskKey);

	public List<JobDependency> getJobDependencies(JobKey jobKey);

}
