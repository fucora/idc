package com.iwellmass.dispatcher.admin.service;

import com.iwellmass.common.ServiceResult;

/**
 * Created by xkwu on 2016/11/1.
 */
public interface IRunningTaskService {
    /**
     * 查询正在执行的任务列表
     *
     * @return
     */
    ServiceResult runningTaskTable();

    int deleteRunningTask(Long id);
}
