package com.iwellmass.dispatcher.admin.service;

import java.util.Map;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.Pager;
import com.iwellmass.dispatcher.admin.dao.model.DdcAlarmHistory;

/**
 * Created by xkwu on 2016/7/11.
 */
public interface IAlarmHistoryService {
    /**
     * Alarm history table table data result.
     * 根据appId查询报警历史
     * @param appId     the app id
     * @param page      the page
     * @param startTime the start time
     * @param endTime   the end time
     * @return the table data result
     */
    ServiceResult alarmHistoryTable(int appId, DdcAlarmHistory alarmHistory, Pager page, String startTime, String endTime);

    /**
     * Alarm history table table data result.
     * 查询所有报警历史
     * @param page      the page
     * @param startTime the start time
     * @param endTime   the end time
     * @return the table data result
     */
    ServiceResult alarmHistoryTable(DdcAlarmHistory alarmHistory,Pager page,String startTime,String endTime);

    Map<String,Integer> alarmHistoryInfo(int appId);
}
