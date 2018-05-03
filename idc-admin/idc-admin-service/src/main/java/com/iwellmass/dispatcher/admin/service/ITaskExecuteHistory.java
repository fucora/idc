package com.iwellmass.dispatcher.admin.service;

import com.iwellmass.dispatcher.admin.dao.model.DdcSubtaskExecuteHistoryEx;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskExecuteHistoryEx;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

/**
 * 任务执行历史查询
 *
 * @author duheng
 */
public interface ITaskExecuteHistory {

    /**
     * 查询任务执行历史
     *
     * @param history
     * @return
     */

    TableDataResult taskHistoryTable(int appId, DdcTaskExecuteHistoryEx history);

    /**
     * 查询流程任务对应的子任务的执行历史
     *
     * @param history
     * @return
     */

    TableDataResult subTaskHistoryTable(int appId, DdcSubtaskExecuteHistoryEx history);
}
