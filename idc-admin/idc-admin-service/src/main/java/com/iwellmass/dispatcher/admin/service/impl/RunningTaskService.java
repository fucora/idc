package com.iwellmass.dispatcher.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcRunningTaskMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcRunningTask;
import com.iwellmass.dispatcher.admin.dao.model.DdcRunningTaskExample;
import com.iwellmass.dispatcher.admin.service.IRunningTaskService;
import com.iwellmass.dispatcher.admin.service.aspect.DdcAdminPermission;

/**
 * Created by xkwu on2016/11/1.
 */
@Service
public class RunningTaskService implements IRunningTaskService {
    @Autowired
    private DdcRunningTaskMapper ddcRunningTaskMapper;

    @Override
    @DdcAdminPermission
    public PageData<DdcRunningTask> runningTaskTable() {
    	List<DdcRunningTask> data = ddcRunningTaskMapper.selectByExample(new DdcRunningTaskExample());
    	
        return new PageData<>(data.size(), data);
    }

    @Override
    @DdcAdminPermission
    public int deleteRunningTask(Long id) {
        return ddcRunningTaskMapper.deleteByPrimaryKey(id);
    }
}
