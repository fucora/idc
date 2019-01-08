package com.iwellmass.idc;

import java.util.List;

import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.TaskKey;

public interface DependencyService {
	
	public List<TaskKey> getSuccessors(String workflowId, TaskKey taskKey);
	
	public List<TaskKey> getPredecessors(String workflowId, TaskKey taskKey);

	public List<JobDependency> getJobDependencies(JobKey jobKey);

	public List<TaskKey> getSuccessors(TaskKey workflowTaskKey, TaskKey subTaskKey);

}
