package com.iwellmass.dispatcher.admin.service.domain;

import java.util.List;

import com.iwellmass.dispatcher.admin.dao.Page;

/**
 * Created by xkwu on 2016/5/10.
 */
public class TableDataResult extends DataResult {
    private Page page;
    private List dataList;

    public TableDataResult() {

    }
    public TableDataResult(List dataList){
        this.dataList = dataList;
    }
    public TableDataResult(Page page, List dataList, Integer count) {
        if (count == null) {
            count = 0;
        }
        this.page = page;
        this.page.setCount(count);
        this.dataList = dataList;
    }

    public List getDataList() {
        return dataList;
    }

    public void setDataList(List dataList) {
        this.dataList = dataList;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
