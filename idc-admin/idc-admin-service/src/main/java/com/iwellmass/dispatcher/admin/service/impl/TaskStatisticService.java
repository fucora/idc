package com.iwellmass.dispatcher.admin.service.impl;

import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskStatisticMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatisticEx;
import com.iwellmass.dispatcher.admin.service.ITaskStatisticService;
import com.iwellmass.dispatcher.admin.service.aspect.DdcPermission;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

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
    public TableDataResult taskStatisticTable(int appId,DdcTaskStatisticEx taskStatisticEx) {
        return new TableDataResult(taskStatisticEx.getPage(), taskStatisticMapper.selectByExampleEx(taskStatisticEx), taskStatisticMapper.countByExampleEx(taskStatisticEx));
    }

	@Override
    @DdcPermission
	public TableDataResult subTaskStatisticTable(int appId,DdcTaskStatisticEx taskStatisticEx) {
		return new TableDataResult(taskStatisticEx.getPage(), taskStatisticMapper.selectSubTaskByExampleEx(taskStatisticEx), taskStatisticMapper.countSubTaskByExampleEx(taskStatisticEx));
	}
}
