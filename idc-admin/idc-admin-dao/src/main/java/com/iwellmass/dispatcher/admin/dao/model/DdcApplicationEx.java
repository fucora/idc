package com.iwellmass.dispatcher.admin.dao.model;

import com.iwellmass.dispatcher.admin.dao.IDCPager;

/**
 * Created by xkwu on 2016/6/21.
 */
public class DdcApplicationEx extends DdcApplication {
    private IDCPager page;
    private Boolean enableAlarm;

    private Integer userId;

    public Boolean getEnableAlarm() {
        return enableAlarm;
    }

    public void setEnableAlarm(Boolean enableAlarm) {
        this.enableAlarm = enableAlarm;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public IDCPager getPage() {
        return page;
    }

    public void setPage(IDCPager page) {
        this.page = page;
    }
}
