package com.iwellmass.dispatcher.admin.service;

import java.util.List;

import com.iwellmass.dispatcher.admin.dao.model.DdcServer;

/**
 * Created by xkwu on 2016/6/20.
 */
public interface IServerService {

    /**
     * List server table table data result.
     *  获取DDC_SERVER表的数据
     * @return the table data result
     */
	List<DdcServer> listServerTable();
}
