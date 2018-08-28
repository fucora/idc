package com.iwellmass.idc.model;

/**
 * Created by xkwu on 2016/5/13.
 */
public class DdcTaskExecuteStatusEx extends DdcTaskExecuteStatus {
    private DdcTask task;

    public DdcTask getTask() {
        return task;
    }

    public void setTask(DdcTask task) {
        this.task = task;
    }
}
