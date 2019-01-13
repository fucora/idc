package com.iwellmass.idc.quartz;

import java.util.List;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.WorkflowEdge;

/**
 * 提供 {@link Task} & {@link Job} 相关服务
 */
public interface IDCPluginService {
	
	IDCPluginConfig getConfig();
	
	//  ~~ Task ~~
	Task getTask(TaskKey taskKey);
	
	// ~~ Job ~~
	Job getJob(JobKey jobKey);
	void saveJob(Job job);
	default JobKey acquireJobKey(Task task) {
		return new JobKey(task.getTaskId(), task.getTaskGroup());
	}
	
	// ~~ dependencies ~~
	List<TaskKey> getSuccessors(TaskKey parentTaskKey, TaskKey taskKey);
	List<TaskKey> getPredecessors(TaskKey parentTaskKey, TaskKey taskKey);
	List<JobDependency> getJobDependencies(JobKey jobKey);
	List<WorkflowEdge> getTaskDependencies(TaskKey taskKey);

}
