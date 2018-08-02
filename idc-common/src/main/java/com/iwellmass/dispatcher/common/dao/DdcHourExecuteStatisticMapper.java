package com.iwellmass.dispatcher.common.dao;

import org.apache.ibatis.annotations.Param;

import com.iwellmass.dispatcher.common.model.DdcHourExecuteStatistic;

import java.util.List;

public interface DdcHourExecuteStatisticMapper {
    List<DdcHourExecuteStatistic> aggregateHistory(@Param("startTime") String startTime, @Param("endTime") String endTime);
    void insertList(@Param("list") List<DdcHourExecuteStatistic> list);
}