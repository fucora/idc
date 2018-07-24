package com.iwellmass.dispatcher.admin.web.controller.admin;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.Pager;
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
 * Created by xkwu on 2016/7/13.
 */
@RequestMapping("/admin/alarmHistoryManage")
@Controller
public class AlarmHistoryManageController {
    private static Logger logger = LoggerFactory.getLogger(AlarmHistoryManageController.class);

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
    public ServiceResult alarmHistoryTable(DdcAlarmHistory alarmHistory, Pager page, String beginTime, String endTime) {
        ServiceResult result = new ServiceResult();
        try {
            result = alarmHistoryService.alarmHistoryTable(alarmHistory,page,beginTime,endTime);
        } catch (Exception e) {
            logger.error("查询报警历史表单失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }
}
