package com.iwellmass.dispatcher.common.dao;

import java.util.List;

import com.iwellmass.dispatcher.common.model.DdcFiveMinuteExecuteStatistic;

public interface DdcFiveMinuteExecuteStatisticMapper {
    List<DdcFiveMinuteExecuteStatistic> aggregateHistory(@Param("startTime") String startTime, @Param("endTime") String endTime);
    List<DdcFiveMinuteExecuteStatistic> aggregateSubHistory(@Param("startTime") String startTime,@Param("endTime") String endTime);
    void insertList(@Param("list") List<DdcFiveMinuteExecuteStatistic> list);
}