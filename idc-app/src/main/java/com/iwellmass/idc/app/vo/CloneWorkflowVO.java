package com.iwellmass.idc.app.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author nobita chen
 * @email nobita0522@qq.com
 * @date 2019/8/23 15:55
 * @description used for clone a new workflow
 */
@Getter
@Setter
public class CloneWorkflowVO {

    @ApiModelProperty("待克隆工作流id")
    private String oldWorkflowId;

    @ApiModelProperty("新克隆出的工作流id")
    private String newWorkflowId;

    @ApiModelProperty("新工作流名称")
    private String workflowName;

    @ApiModelProperty("新工作流描述")
    private String description;
}
