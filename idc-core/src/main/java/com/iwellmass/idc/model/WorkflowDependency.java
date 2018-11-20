package com.iwellmass.idc.model;

import lombok.Data;

@Data
public class WorkflowDependency {
	
	private Integer id;
	
	private Integer workflowId;
	
	private String srcTaskId;
	
	private String srcTaskGroup;
	
	private String taskId;
	
	private String groupId;
	
}
