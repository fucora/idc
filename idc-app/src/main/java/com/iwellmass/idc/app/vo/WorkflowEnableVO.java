package com.iwellmass.idc.app.vo;

import com.iwellmass.idc.model.TaskDependency;
import com.iwellmass.idc.model.TaskKey;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkflowEnableVO {

    private String workflowId;

    private List<TaskDependency> taskDependencies;
}
