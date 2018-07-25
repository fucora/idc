package com.iwellmass.dispatcher.admin.service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.model.DdcRunningTask;

/**
 * Created by xkwu on 2016/11/1.
 */
public interface IRunningTaskService {
    /**
     * 查询正在执行的任务列表
     *
     * @return
     */
	PageData<DdcRunningTask> runningTaskTable();

    int deleteRunningTask(Long id);
}
