package com.iwellmass.dispatcher.admin.service.impl;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskStatisticMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatisticEx;
import com.iwellmass.dispatcher.admin.service.ITaskStatisticService;
import com.iwellmass.dispatcher.admin.service.aspect.DdcPermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xkwu on 2016/5/24.
 */
@Service
public class TaskStatisticService implements ITaskStatisticService {
    @Autowired
    private DdcTaskStatisticMapper taskStatisticMapper;

    @Override
    @DdcPermission
    public ServiceResult taskStatisticTable(int appId,DdcTaskStatisticEx taskStatisticEx) {
        return new ServiceResult(taskStatisticEx.getPage(), taskStatisticMapper.selectByExampleEx(taskStatisticEx), taskStatisticMapper.countByExampleEx(taskStatisticEx));
    }

	@Override
    @DdcPermission
	public ServiceResult subTaskStatisticTable(int appId,DdcTaskStatisticEx taskStatisticEx) {
		return new ServiceResult(taskStatisticEx.getPage(), taskStatisticMapper.selectSubTaskByExampleEx(taskStatisticEx), taskStatisticMapper.countSubTaskByExampleEx(taskStatisticEx));
	}
}
