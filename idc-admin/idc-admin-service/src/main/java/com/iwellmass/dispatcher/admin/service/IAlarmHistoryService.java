package com.iwellmass.dispatcher.admin.service;

import com.iwellmass.dispatcher.admin.dao.Page;
import com.iwellmass.dispatcher.admin.dao.model.DdcAlarmHistory;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

import java.util.Map;

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
    TableDataResult alarmHistoryTable(int appId, DdcAlarmHistory alarmHistory, Page page, String startTime, String endTime);

    /**
     * Alarm history table table data result.
     * 查询所有报警历史
     * @param page      the page
     * @param startTime the start time
     * @param endTime   the end time
     * @return the table data result
     */
    TableDataResult alarmHistoryTable(DdcAlarmHistory alarmHistory,Page page,String startTime,String endTime);

    Map<String,Integer> alarmHistoryInfo(int appId);
}
