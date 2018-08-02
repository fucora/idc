package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

public class JobQuery {

    @ApiModelProperty("任务名")
    private String name;

    @ApiModelProperty("任务类型")
    private String taskType;

    @ApiModelProperty("负责人")
    private String assignee;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
