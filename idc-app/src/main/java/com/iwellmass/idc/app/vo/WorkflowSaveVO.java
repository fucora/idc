package com.iwellmass.idc.app.vo;

import com.iwellmass.idc.model.TaskDependency;
import com.iwellmass.idc.model.Workflow;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkflowSaveVO {

    private Workflow workflow;

    private List<TaskDependency> taskDependencies;
}
