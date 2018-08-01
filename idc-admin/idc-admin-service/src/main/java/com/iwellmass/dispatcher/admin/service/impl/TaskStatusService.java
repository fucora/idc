package com.iwellmass.dispatcher.admin.service.impl;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.IDCPager;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskExecuteStatusMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskExecuteStatus;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskExecuteStatusExample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xkwu on 2016/5/13.
 */
@Service
public class TaskStatusService {
    @Autowired
    private DdcTaskExecuteStatusMapper statusMapper;
    public PageData<DdcTaskExecuteStatus> taskStatusTable(DdcTaskExecuteStatus status, IDCPager page) {
        DdcTaskExecuteStatusExample ddcTaskExecuteStatusExample = new DdcTaskExecuteStatusExample();
        ddcTaskExecuteStatusExample.setPage(page);
        DdcTaskExecuteStatusExample.Criteria taskExecuteStatusCriteria = ddcTaskExecuteStatusExample.createCriteria();
        if(status.getExecuteId() !=null) {
            taskExecuteStatusCriteria.andExecuteIdEqualTo(status.getExecuteId());
        }
        if(status.getTaskId() != null){
            taskExecuteStatusCriteria.andTaskIdEqualTo(status.getTaskId());
        }
        if(status.getWorkflowExecuteId() != null){
            taskExecuteStatusCriteria.andWorkflowExecuteIdEqualTo(status.getWorkflowExecuteId());
        }
        
        ddcTaskExecuteStatusExample.setOrderByClause("TIMESTAMP ASC, ID ASC");
        
        return new PageData<>(statusMapper.countByExample(ddcTaskExecuteStatusExample), statusMapper.selectByExample(ddcTaskExecuteStatusExample));
    }
}
