package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;
import java.util.Date;

public class JobQuery {

    @ApiModelProperty("任务名")
    private String taskName;

    @ApiModelProperty("任务类型")
    private String contentType;

    @ApiModelProperty("负责人")
    private String assignee;

    @ApiModelProperty("业务时期")
    private Timestamp loadTime;

    @ApiModelProperty("运行时期")
    private Timestamp excuteTime;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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

    public void setLoadTime(Timestamp loadTime) {
        this.loadTime = loadTime;
    }

    public Timestamp getExcuteTime() {
        return excuteTime;
    }

    public void setExcuteTime(Timestamp excuteTime) {
        this.excuteTime = excuteTime;
    }
}
