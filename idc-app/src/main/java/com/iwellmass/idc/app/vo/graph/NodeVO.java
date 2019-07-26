package com.iwellmass.idc.app.vo.graph;

import com.iwellmass.idc.scheduler.model.TaskType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(of = {"id"})
public class NodeVO {

	private String id;
	
	private String taskId;

	private String taskName;
	
	private TaskType taskType;
	
	private String domain;

	private String type;
	
}
