package com.iwellmass.dispatcher.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskStatisticMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatistic;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatisticEx;
import com.iwellmass.dispatcher.admin.service.ITaskStatisticService;
import com.iwellmass.dispatcher.admin.service.aspect.DdcPermission;

/**
 * Created by xkwu on 2016/5/24.
 */
@Service
public class TaskStatisticService implements ITaskStatisticService {
    @Autowired
    private DdcTaskStatisticMapper taskStatisticMapper;

    @Override
    @DdcPermission
    public PageData<DdcTaskStatistic> taskStatisticTable(int appId,DdcTaskStatisticEx taskStatisticEx) {
        return new PageData<>(taskStatisticMapper.countByExampleEx(taskStatisticEx), taskStatisticMapper.selectByExampleEx(taskStatisticEx));
    }

	@Override
    @DdcPermission
	public PageData<DdcTaskStatistic> subTaskStatisticTable(int appId,DdcTaskStatisticEx taskStatisticEx) {
		return new PageData<>(taskStatisticMapper.countSubTaskByExampleEx(taskStatisticEx), taskStatisticMapper.selectSubTaskByExampleEx(taskStatisticEx));
	}
}
