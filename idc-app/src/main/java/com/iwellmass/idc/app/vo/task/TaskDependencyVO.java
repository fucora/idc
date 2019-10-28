package com.iwellmass.idc.app.vo.task;

import com.iwellmass.idc.scheduler.model.TaskDependency;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDependencyVO {

    @ApiModelProperty("依赖的调度计划名(先执行，作为source角色)")
    private String source;

    @ApiModelProperty("依赖规则")
    private TaskDependency.Principle principle;
}
