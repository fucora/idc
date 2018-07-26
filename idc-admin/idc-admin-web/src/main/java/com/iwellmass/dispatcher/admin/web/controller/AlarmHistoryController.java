package com.iwellmass.dispatcher.admin.web.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.IDCPager;
import com.iwellmass.dispatcher.admin.dao.model.DdcAlarmHistory;
import com.iwellmass.dispatcher.admin.service.IAlarmHistoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xkwu on 2016/7/11.
 */
@Controller
@RequestMapping("alarmHistory")
public class AlarmHistoryController {
    private static Logger logger = LoggerFactory.getLogger(AlarmHistoryController.class);

    @Autowired
    private IAlarmHistoryService alarmHistoryService;

    /**
     * Alarm history table table data result.
     *根据appId查询报警历史表单
     * @param alarmHistory the alarm history
     * @param page         the page
     * @param beginTime    the begin time
     * @param endTime      the end time
     * @return the table data result
     */
    @RequestMapping(value = "/alarmHistoryTable", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<PageData<DdcAlarmHistory>> alarmHistoryTable(DdcAlarmHistory alarmHistory, IDCPager page, String beginTime, String endTime) {
    	
    	
    	PageData<DdcAlarmHistory> result = alarmHistoryService.alarmHistoryTable(alarmHistory.getAppId().intValue(),alarmHistory,page,beginTime,endTime);
    	return ServiceResult.success(result);
    }
}
