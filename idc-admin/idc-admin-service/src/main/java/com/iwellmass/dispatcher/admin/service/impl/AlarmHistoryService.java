package com.iwellmass.dispatcher.admin.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.Pager;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcAlarmHistoryMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcAlarmHistory;
import com.iwellmass.dispatcher.admin.dao.model.DdcAlarmHistoryExample;
import com.iwellmass.dispatcher.admin.service.IAlarmHistoryService;
import com.iwellmass.dispatcher.admin.service.aspect.DdcAdminPermission;
import com.iwellmass.dispatcher.admin.service.aspect.DdcPermission;
import com.iwellmass.dispatcher.common.utils.DateUtils;

/**
 * Created by xkwu on 2016/71.
 */
@Service
public class AlarmHistoryService implements IAlarmHistoryService {
    private static Logger logger = LoggerFactory.getLogger(AlarmHistoryService.class);
    @Autowired
    private DdcAlarmHistoryMapper alarmHistoryMapper;

    @Override
    @DdcPermission
    public PageData<DdcAlarmHistory> alarmHistoryTable(int appId, DdcAlarmHistory alarmHistory, Pager page, String startTime, String endTime) {
        DdcAlarmHistoryExample alarmHistoryExample = new DdcAlarmHistoryExample();
        alarmHistoryExample.setPage(page);
        alarmHistoryExample.setOrderByClause("ALARM_DATE DESC");
        DdcAlarmHistoryExample.Criteria alarmHistoryCriteria = alarmHistoryExample.createCriteria();
        try {
            alarmHistoryCriteria.andAlarmDateBetween(DateUtils.parseDate(startTime,"yyyy-MM-dd HH:mm:ss"),DateUtils.parseDate(endTime,"yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            logger.error("日期转换失败,startTime:{} endTime:{}",startTime,endTime);
        }
        if(alarmHistory.getTaskId()!=null){
            alarmHistoryCriteria.andTaskIdEqualTo(alarmHistory.getTaskId());
        }
        alarmHistoryCriteria.andAppIdEqualTo(appId);
        
        List<DdcAlarmHistory> data = alarmHistoryMapper.selectByExample(alarmHistoryExample);
        
        return new PageData<>(alarmHistoryMapper.countByExample(alarmHistoryExample), data);
    }

    @Override
    @DdcAdminPermission
    public PageData<DdcAlarmHistory> alarmHistoryTable(DdcAlarmHistory alarmHistory,Pager page,String startTime,String endTime) {
        DdcAlarmHistoryExample alarmHistoryExample = new DdcAlarmHistoryExample();
        alarmHistoryExample.setPage(page);
        alarmHistoryExample.setOrderByClause("ALARM_DATE DESC");
        DdcAlarmHistoryExample.Criteria alarmHistoryCriteria = alarmHistoryExample.createCriteria();
        try {
            alarmHistoryCriteria.andAlarmDateBetween(DateUtils.parseDate(startTime,"yyyy-MM-dd HH:mm:ss"),DateUtils.parseDate(endTime,"yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            logger.error("日期转换失败,startTime:{} endTime:{}",startTime,endTime);
        }
        if(alarmHistory.getAppId()!=null){
            alarmHistoryCriteria.andAppIdEqualTo(alarmHistory.getAppId());
        }
        if(alarmHistory.getTaskId()!=null){
            alarmHistoryCriteria.andTaskIdEqualTo(alarmHistory.getTaskId());
        }
        List<DdcAlarmHistory> data = alarmHistoryMapper.selectByExample(alarmHistoryExample);
        return new PageData<>(alarmHistoryMapper.countByExample(alarmHistoryExample), data);
    }

    @Override
    @DdcPermission
    public Map<String, Integer> alarmHistoryInfo(int appId) {
        Map<String,Integer> map = new HashMap<>();

        DdcAlarmHistoryExample alarmHistoryExample = new DdcAlarmHistoryExample();
        DdcAlarmHistoryExample.Criteria alarmHistoryCriteria = alarmHistoryExample.createCriteria();
        alarmHistoryCriteria.andAppIdEqualTo(appId);
        map.put("amount_alarm",alarmHistoryMapper.countByExample(alarmHistoryExample));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate =calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endDate=calendar.getTime();
        alarmHistoryCriteria.andAlarmDateBetween(startDate,endDate);
        map.put("today_alarm",alarmHistoryMapper.countByExample(alarmHistoryExample));

        return map;
    }
}
