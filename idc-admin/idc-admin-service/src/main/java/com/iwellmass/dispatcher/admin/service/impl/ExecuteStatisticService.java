package com.iwellmass.dispatcher.admin.service.impl;

import com.iwellmass.dispatcher.admin.dao.mapper.DdcFiveMinuteExecuteStatisticMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcHourExecuteStatisticMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcFiveMinuteExecuteStatistic;
import com.iwellmass.dispatcher.admin.dao.model.DdcFiveMinuteExecuteStatisticExample;
import com.iwellmass.dispatcher.admin.dao.model.DdcHourExecuteStatistic;
import com.iwellmass.dispatcher.admin.dao.model.DdcHourExecuteStatisticExample;
import com.iwellmass.dispatcher.admin.service.IExecuteStatisticService;
import com.iwellmass.dispatcher.admin.service.aspect.DdcPermission;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

/**
 * Created by xkwu on 2016/6/30.
 */
@Service
public class ExecuteStatisticService implements IExecuteStatisticService {
    private static Logger logger = LoggerFactory.getLogger(ExecuteStatisticService.class);
    @Autowired
    private DdcFiveMinuteExecuteStatisticMapper fiveMinuteExecuteStatisticMapper;

    @Autowired
    private DdcHourExecuteStatisticMapper hourExecuteStatisticMapper;


    @Override
    @DdcPermission
    public List<DdcFiveMinuteExecuteStatistic> aggregateByAppId(int appId, String startTime) {
        return fiveMinuteExecuteStatisticMapper.aggregateByAppId(appId, startTime);
    }

    @Override
    @DdcPermission
    public List<DdcFiveMinuteExecuteStatistic> fiveMinuteAggregateByAppIdAndTaskId(int appId, int taskId, String startTime) {
        DdcFiveMinuteExecuteStatisticExample executeStatisticExample = new DdcFiveMinuteExecuteStatisticExample();
        DdcFiveMinuteExecuteStatisticExample.Criteria statisticCriteria = executeStatisticExample.createCriteria();
        statisticCriteria.andAppIdEqualTo(appId);
        statisticCriteria.andTaskIdEqualTo(taskId);
        try {
            statisticCriteria.andDataTimeGreaterThan(DateUtils.parseDate(startTime, new String[]{"yyyy-MM-dd HH:mm:ss"}));
        } catch (ParseException e) {
            logger.error("日期转换失败", e);
        }
        return fiveMinuteExecuteStatisticMapper.selectByExample(executeStatisticExample);
    }

    @Override
    @DdcPermission
    public List<DdcHourExecuteStatistic> hourAggregateByAppIdAndTaskId(int appId, int taskId, String startTime) {
        DdcHourExecuteStatisticExample executeStatisticExample = new DdcHourExecuteStatisticExample();
        DdcHourExecuteStatisticExample.Criteria statisticCriteria = executeStatisticExample.createCriteria();
        statisticCriteria.andAppIdEqualTo(appId);
        statisticCriteria.andTaskIdEqualTo(taskId);
        try {
            statisticCriteria.andDataTimeGreaterThan(DateUtils.parseDate(startTime, new String[]{"yyyy-MM-dd HH:mm:ss"}));
        } catch (ParseException e) {
            logger.error("日期转换失败", e);
        }
        return hourExecuteStatisticMapper.selectByExample(executeStatisticExample);
    }


}
