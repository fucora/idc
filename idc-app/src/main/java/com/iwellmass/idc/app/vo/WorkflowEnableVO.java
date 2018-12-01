package com.iwellmass.idc.app.vo;

import java.util.List;

import com.iwellmass.idc.model.TaskDependency;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowEnableVO {

    private String graphId;

    private List<TaskDependency> taskDependencies;
}
