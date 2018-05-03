package com.iwellmass.dispatcher.admin.service;

import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

/**
 * Created by xkwu on 2016/6/20.
 */
public interface IServerService {

    /**
     * List server table table data result.
     *  获取DDC_SERVER表的数据
     * @return the table data result
     */
    TableDataResult listServerTable();
}
