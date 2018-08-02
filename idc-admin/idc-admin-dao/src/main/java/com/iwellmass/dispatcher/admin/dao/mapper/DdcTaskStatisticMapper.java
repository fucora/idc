package com.iwellmass.dispatcher.admin.dao.mapper;

import java.util.List;

import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatistic;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatisticEx;

/**
 * Created by xkwu on 2016/5/24.
 */
public interface DdcTaskStatisticMapper {
    List<DdcTaskStatistic> selectByExampleEx(DdcTaskStatisticEx map);

    Integer countByExampleEx(DdcTaskStatisticEx map);

    List<DdcTaskStatistic> selectSubTaskByExampleEx(DdcTaskStatisticEx map);

    Integer countSubTaskByExampleEx(DdcTaskStatisticEx map);
}
