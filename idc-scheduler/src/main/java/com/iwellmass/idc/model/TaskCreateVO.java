package com.iwellmass.idc.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateVO {

    private String taskGroup;

    private String taskName;

    private String description;

    private TaskType taskType;

    private String contentType;

    private DispatchType dispatchType;

    private Integer workflowId;
}
