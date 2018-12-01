package com.iwellmass.idc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDependency {
	
	public TaskDependency() {}

    public TaskDependency(String srcTaskId, String srcTaskGroup, String taskId, String taskGroup) {
        this.srcTaskId = srcTaskId;
        this.srcTaskGroup = srcTaskGroup;
        this.taskId = taskId;
        this.taskGroup = taskGroup;
    }

    private String srcTaskId;

    private String srcTaskGroup;

    private String taskId;

    private String taskGroup;
}
