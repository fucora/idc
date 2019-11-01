package com.iwellmass.idc.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskDependencyQueryVO {

    @ApiModelProperty("包含该计划名的调度计划依赖图")
    private String taskName;

    @ApiModelProperty("调度计划依赖图名")
    private String name;
}
