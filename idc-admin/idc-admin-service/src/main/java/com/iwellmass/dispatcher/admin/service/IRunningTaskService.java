package com.iwellmass.dispatcher.admin.service;

import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

/**
 * Created by xkwu on 2016/11/1.
 */
public interface IRunningTaskService {
    /**
     * 查询正在执行的任务列表
     *
     * @return
     */
    TableDataResult runningTaskTable();

    int deleteRunningTask(Long id);
}
