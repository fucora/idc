package com.iwellmass.dispatcher.admin.dao.model;

import com.iwellmass.dispatcher.admin.dao.IDCPager;

/**
 * Created by xkwu on 2016/5/13.
 */
public class DdcTaskExecuteHistoryEx extends DdcTaskExecuteHistory {
    private String beginTime;
    private String endTime;
    private DdcTask task;
    private IDCPager page;

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

    public IDCPager getPage() {
        return page;
    }

    public void setPage(IDCPager page) {
        this.page = page;
    }
}
