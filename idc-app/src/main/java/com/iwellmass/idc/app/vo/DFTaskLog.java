package com.iwellmass.idc.app.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chenxiong
 * @email nobita0522@qq.com
 * @date 2018/12/24 20:21
 * @description
 */
public class DFTaskLog implements Serializable {
    private Long id;
    private Long taskId;
    private String execParams;
    private String execContent;
    private String loadDate;
    private String hiveTableName;
    private String ftpName;
    private Long ftpId;
    private Date createTime;
    private String logs;
    private String result;

    public DFTaskLog() {
    }

    public DFTaskLog(Long taskId, String execParams, String execContent, String logs, String result, String loadDate, String hiveTableName, String ftpName, Long ftpId) {
        this.taskId = taskId;
        this.execParams = execParams;
        this.execContent = execContent;
        this.logs = logs;
        this.result = result;
        this.loadDate = loadDate;
        this.hiveTableName = hiveTableName;
        this.ftpName = ftpName;
        this.ftpId = ftpId;
        this.createTime = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getExecParams() {
        return execParams;
    }

    public void setExecParams(String execParams) {
        this.execParams = execParams;
    }

    public String getExecContent() {
        return execContent;
    }

    public void setExecContent(String execContent) {
        this.execContent = execContent;
    }

    public String getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(String loadDate) {
        this.loadDate = loadDate;
    }

    public String getHiveTableName() {
        return hiveTableName;
    }

    public void setHiveTableName(String hiveTableName) {
        this.hiveTableName = hiveTableName;
    }

    public String getFtpName() {
        return ftpName;
    }

    public void setFtpName(String ftpName) {
        this.ftpName = ftpName;
    }

    public Long getFtpId() {
        return ftpId;
    }

    public void setFtpId(Long ftpId) {
        this.ftpId = ftpId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
