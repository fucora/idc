package com.iwellmass.dispatcher.admin.service.impl;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
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
    public PageData<DdcTaskExecuteHistoryEx> taskHistoryTable(int appId,DdcTaskExecuteHistoryEx history) {

        return new PageData<>(historyMapper.countByExampleEx(history), historyMapper.selectByExampleEx(history));
    }
    @DdcPermission
    public PageData<DdcSubtaskExecuteHistoryEx> subTaskHistoryTable(int appId,DdcSubtaskExecuteHistoryEx history) {

        return new PageData<>(subtaskExecuteHistoryMapper.countByExampleEx(history), subtaskExecuteHistoryMapper.selectByExampleEx(history));
    }
}
