package com.iwellmass.dispatcher.admin.dao.model;

import com.iwellmass.dispatcher.admin.dao.Page;

/**
 * Created by xkwu on 2016/6/21.
 */
public class DdcApplicationEx extends DdcApplication {
    private Page page;
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

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
