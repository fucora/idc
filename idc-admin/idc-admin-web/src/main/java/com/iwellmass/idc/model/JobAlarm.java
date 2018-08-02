package com.iwellmass.idc.model;


import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 任务预警
 */

public class JobAlarm {
    private Integer id;

    @ApiModelProperty("任务的Id")
    private Integer taskId;

    @ApiModelProperty("任务名")
    private String taskName;

    @ApiModelProperty("报警的原因")
    private String cause;

    @ApiModelProperty("接收者")
    private String receivers;

    @ApiModelProperty("创建者")
    private String creator;

    @ApiModelProperty("报警时间")
    private Date alarmTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getReceivers() {
        return receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    @Override
    public String toString() {
        return "JobAlarm{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", cause='" + cause + '\'' +
                ", receivers='" + receivers + '\'' +
                ", creator='" + creator + '\'' +
                ", alarmTime=" + alarmTime +
                '}';
    }
}
