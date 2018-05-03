package com.iwellmass.dispatcher.admin.service.impl;

import com.iwellmass.dispatcher.admin.dao.mapper.DdcSubtaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcSubtaskExecuteHistoryEx;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskExecuteHistoryEx;
import com.iwellmass.dispatcher.admin.service.ITaskExecuteHistory;
import com.iwellmass.dispatcher.admin.service.aspect.DdcPermission;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xkwu on 2016/5/13.
 */
@Service
public class TaskExecuteHistoryService implements ITaskExecuteHistory {
    @Autowired
    private DdcTaskExecuteHistoryMapper historyMapper;

    @Autowired
    private DdcSubtaskExecuteHistoryMapper subtaskExecuteHistoryMapper;

    @DdcPermission
    public TableDataResult taskHistoryTable(int appId,DdcTaskExecuteHistoryEx history) {

        return new TableDataResult(history.getPage(), historyMapper.selectByExampleEx(history), historyMapper.countByExampleEx(history));
    }
    @DdcPermission
    public TableDataResult subTaskHistoryTable(int appId,DdcSubtaskExecuteHistoryEx history) {

        return new TableDataResult(history.getPage(), subtaskExecuteHistoryMapper.selectByExampleEx(history), subtaskExecuteHistoryMapper.countByExampleEx(history));
    }
}
