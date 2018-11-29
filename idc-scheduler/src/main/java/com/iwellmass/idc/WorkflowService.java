package com.iwellmass.idc;

import java.util.List;

import org.jgrapht.Graph;

import com.iwellmass.idc.model.TaskEdge;
import com.iwellmass.idc.model.TaskKey;

public interface WorkflowService {
	
	public static final TaskKey START = new TaskKey("start", "idc");
	
	public static final TaskKey END = new TaskKey("end", "idc");
	
	public Graph<TaskKey, TaskEdge> getWorkflow(TaskKey taskKey);
	
	public List<TaskKey> getSuccessors(int workflowId, TaskKey taskKey);
	
	public List<TaskKey> getPredecessors(int workflowId, TaskKey taskKey);

}
