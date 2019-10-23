package com.iwellmass.idc.app.vo.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Administrator on 2019/9/27.
 */
@Data
@ApiModel("手动调度修改Vo")
public class ManualUpdateVo {


    @ApiModelProperty("计划名称")
    String taskName;

    @ApiModelProperty("描述")
    String description;

    @ApiModelProperty("负责人")
    String assignee;

    @ApiModelProperty("失败重试")
    Boolean isRetry = true;

    @ApiModelProperty("出错时阻塞")
    Boolean blockOnError = true;




}
