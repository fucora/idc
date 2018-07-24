package com.iwellmass.dispatcher.admin.dao.model;

import com.iwellmass.dispatcher.admin.dao.Pager;

/**
 * Created by xkwu on 2016/5/27.
 */
public class DdcSubtaskExecuteHistoryEx extends DdcSubtaskExecuteHistory {
    private String beginTime;
    private String endTime;
    private DdcTask task;
    private Pager page;

    public DdcTask getTask() {
        return task;
    }

    public void setTask(DdcTask task) {
        this.task = task;
    }


    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public Pager getPage() {
        return page;
    }

    public void setPage(Pager page) {
        this.page = page;
    }
}
