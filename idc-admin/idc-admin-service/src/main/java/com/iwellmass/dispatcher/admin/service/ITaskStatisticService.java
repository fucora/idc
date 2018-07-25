package com.iwellmass.dispatcher.admin.service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatistic;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatisticEx;

/**
 * Created by xkwu on 2016/5/24.
 */
public interface ITaskStatisticService {

	PageData<DdcTaskStatistic> taskStatisticTable(int appId, DdcTaskStatisticEx taskStatisticEx);

    PageData<DdcTaskStatistic>  subTaskStatisticTable(int appId, DdcTaskStatisticEx taskStatisticEx);
}
