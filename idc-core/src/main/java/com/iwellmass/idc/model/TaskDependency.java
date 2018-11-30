package com.iwellmass.idc.model;

import com.iwellmass.idc.model.TaskKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDependency {

    private TaskKey srcTaskKey;

    private TaskKey taskKey;
}
