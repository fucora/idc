package com.iwellmass.idc.dag;

import java.util.List;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;

public class WorkflowService {
	
	public static final TaskKey START = new TaskKey("start", "idc");
	
	public static final TaskKey END = new TaskKey("end", "idc");
	
	public List<Task> successors(Integer workflowId, TaskKey taskKey) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
