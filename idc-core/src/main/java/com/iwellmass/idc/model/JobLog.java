package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author chenxiong
 * @email nobita0522@qq.com
 * @date 2018/12/24 20:21
 * @description
 */
public class JobLog implements Serializable {

    @ApiModelProperty("日志id")
    private Long id;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("任务域")
    private String taskGroup;

    @ApiModelProperty("实例id")
    private Integer instanceId;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("详细日志")
    private String log;

    @ApiModelProperty("执行结果")
    private String result;

    public JobLog() {
    }

    public JobLog(Long id, String taskId, String taskGroup, Integer instanceId, LocalDateTime createTime, String log, String result) {
        this.id = id;
        this.taskId = taskId;
        this.taskGroup = taskGroup;
        this.instanceId = instanceId;
        this.createTime = createTime;
        this.log = log;
        this.result = result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public Integer getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Integer instanceId) {
        this.instanceId = instanceId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
