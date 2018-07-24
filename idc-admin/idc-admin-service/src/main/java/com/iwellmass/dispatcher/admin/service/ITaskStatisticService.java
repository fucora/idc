package com.iwellmass.dispatcher.admin.service;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatisticEx;

/**
 * Created by xkwu on 2016/5/24.
 */
public interface ITaskStatisticService {

    ServiceResult taskStatisticTable(int appId, DdcTaskStatisticEx taskStatisticEx);

    ServiceResult subTaskStatisticTable(int appId, DdcTaskStatisticEx taskStatisticEx);
}
