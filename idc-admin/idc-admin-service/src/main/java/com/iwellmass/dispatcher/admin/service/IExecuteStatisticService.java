package com.iwellmass.dispatcher.admin.service;

import java.util.List;

import com.iwellmass.dispatcher.admin.dao.model.DdcFiveMinuteExecuteStatistic;
import com.iwellmass.dispatcher.admin.dao.model.DdcHourExecuteStatistic;

/**
 * Created by xkwu on 2016/6/30.
 */
public interface IExecuteStatisticService {
    List<DdcFiveMinuteExecuteStatistic> aggregateByAppId(int appId, String startTime);

    List<DdcFiveMinuteExecuteStatistic> fiveMinuteAggregateByAppIdAndTaskId(int appId,int taskId,String startTime);

    List<DdcHourExecuteStatistic> hourAggregateByAppIdAndTaskId(int appId, int taskId, String startTime);
}
