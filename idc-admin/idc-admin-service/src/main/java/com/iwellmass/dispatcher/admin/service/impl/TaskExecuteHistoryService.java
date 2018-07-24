package com.iwellmass.dispatcher.admin.service.impl;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcSubtaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcSubtaskExecuteHistoryEx;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskExecuteHistoryEx;
import com.iwellmass.dispatcher.admin.service.ITaskExecuteHistory;
import com.iwellmass.dispatcher.admin.service.aspect.DdcPermission;

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
    public ServiceResult taskHistoryTable(int appId,DdcTaskExecuteHistoryEx history) {

        return new ServiceResult(history.getPage(), historyMapper.selectByExampleEx(history), historyMapper.countByExampleEx(history));
    }
    @DdcPermission
    public ServiceResult subTaskHistoryTable(int appId,DdcSubtaskExecuteHistoryEx history) {

        return new ServiceResult(history.getPage(), subtaskExecuteHistoryMapper.selectByExampleEx(history), subtaskExecuteHistoryMapper.countByExampleEx(history));
    }
}
