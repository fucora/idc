package com.iwellmass.dispatcher.admin.service.impl;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcRunningTaskMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcRunningTaskExample;
import com.iwellmass.dispatcher.admin.service.IRunningTaskService;
import com.iwellmass.dispatcher.admin.service.aspect.DdcAdminPermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xkwu on2016/11/1.
 */
@Service
public class RunningTaskService implements IRunningTaskService {
    @Autowired
    private DdcRunningTaskMapper ddcRunningTaskMapper;

    @Override
    @DdcAdminPermission
    public ServiceResult runningTaskTable() {
        return new ServiceResult(ddcRunningTaskMapper.selectByExample(new DdcRunningTaskExample()));
    }

    @Override
    @DdcAdminPermission
    public int deleteRunningTask(Long id) {
        return ddcRunningTaskMapper.deleteByPrimaryKey(id);
    }
}
