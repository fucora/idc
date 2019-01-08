package com.iwellmass.idc.app.model;

import com.iwellmass.idc.model.Task;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chenxiong
 * @email nobita0522@qq.com
 * @date 2019/01/08 13:58
 * @description
 */
@Getter
@Setter
public class SimpleTaskVO {
    @ApiModelProperty("任务id")
    private String taskId;
    @ApiModelProperty("任务域")
    private String taskGroup;
    @ApiModelProperty("任务名称")
    private String taskName;
    @ApiModelProperty("任务参数")
    private String parameter;

    public SimpleTaskVO() {
    }

    public SimpleTaskVO(Task task) {
        this.taskId = task.getTaskId();
        this.taskGroup = task.getTaskGroup();
        this.taskName = task.getTaskName();
        this.parameter = task.getParameter();
    }
}
