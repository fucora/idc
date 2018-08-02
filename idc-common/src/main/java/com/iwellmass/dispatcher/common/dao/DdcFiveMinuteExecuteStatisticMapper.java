package com.iwellmass.dispatcher.common.dao;

import org.apache.ibatis.annotations.Param;

import com.iwellmass.dispatcher.common.model.DdcFiveMinuteExecuteStatistic;

import java.util.List;

public interface DdcFiveMinuteExecuteStatisticMapper {
    List<DdcFiveMinuteExecuteStatistic> aggregateHistory(@Param("startTime") String startTime, @Param("endTime") String endTime);
    List<DdcFiveMinuteExecuteStatistic> aggregateSubHistory(@Param("startTime") String startTime,@Param("endTime") String endTime);
    void insertList(@Param("list") List<DdcFiveMinuteExecuteStatistic> list);
}