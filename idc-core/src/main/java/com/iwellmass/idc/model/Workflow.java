package com.iwellmass.idc.model;

import lombok.Data;

@Data
public class Workflow {
	
	private Integer workflowId;
	
	private String taskId;
	
	private String taskGroup;
	
	// 前端用，流程都画图
	private String graph;
}
