package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;
import java.util.Date;

public class JobQuery {

    @ApiModelProperty("任务名")
    private String name;

    @ApiModelProperty("任务类型")
    private String taskType;

    @ApiModelProperty("负责人")
    private String assignee;

    @ApiModelProperty("业务时期")
    private Timestamp loadTime;

    @ApiModelProperty("运行时期")
    private Timestamp excuteTime;

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

    public Date getLoadTime() {
        return loadTime;
    }


    @Override
    public String toString() {
        return "JobQuery{" +
                "name='" + name + '\'' +
                ", taskType='" + taskType + '\'' +
                ", assignee='" + assignee + '\'' +
                ", loadTime=" + loadTime +
                ", excuteTime=" + excuteTime +
                '}';
    }
}
