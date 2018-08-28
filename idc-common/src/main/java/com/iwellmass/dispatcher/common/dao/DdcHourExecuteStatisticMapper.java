package com.iwellmass.dispatcher.common.dao;

import java.util.List;

import com.iwellmass.dispatcher.common.model.DdcHourExecuteStatistic;

public interface DdcHourExecuteStatisticMapper {
    List<DdcHourExecuteStatistic> aggregateHistory(@Param("startTime") String startTime, @Param("endTime") String endTime);
    void insertList(@Param("list") List<DdcHourExecuteStatistic> list);
}