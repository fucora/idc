package com.iwellmass.dispatcher.admin.service;

import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatisticEx;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

/**
 * Created by xkwu on 2016/5/24.
 */
public interface ITaskStatisticService {

    TableDataResult taskStatisticTable(int appId, DdcTaskStatisticEx taskStatisticEx);

    TableDataResult subTaskStatisticTable(int appId, DdcTaskStatisticEx taskStatisticEx);
}
