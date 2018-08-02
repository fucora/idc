package com.iwellmass.dispatcher.admin.dao.model;

import com.iwellmass.dispatcher.admin.dao.IDCPager;

/**
 * Created by xkwu on 2016/5/24.
 */
public class DdcTaskStatisticEx extends DdcTaskStatistic {
    private String beginTime;
    private String endTime;
    private IDCPager page;

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public IDCPager getPage() {
        return page;
    }

    public void setPage(IDCPager page) {
        this.page = page;
    }
}
