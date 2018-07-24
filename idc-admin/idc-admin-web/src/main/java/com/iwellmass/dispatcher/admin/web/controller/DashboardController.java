package com.iwellmass.dispatcher.admin.web.controller;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.service.IAlarmHistoryService;
import com.iwellmass.dispatcher.admin.service.IExecuteStatisticService;
import com.iwellmass.dispatcher.admin.service.INodeService;
import com.iwellmass.dispatcher.admin.service.ITaskService;

import java.util.Calendar;

/**
 * Created by xkwu on 2016/6/29.
 */
@Controller
@RequestMapping("/appDashboard")
public class DashboardController {
    private static Logger logger = LoggerFactory.getLogger(ApplicationController.class);
    @Autowired
    private INodeService nodeService;

    @Autowired
    private ITaskService taskService;

    @Autowired
    private IAlarmHistoryService alarmHistoryService;

    @Autowired
    private IExecuteStatisticService executeStatisticService;
    @RequestMapping(value = "/dashboard", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult dashboard(int appId) {
        ServiceResult result = new ServiceResult();
        try {
            result.setResult(nodeService.nodeInfo(appId));
            result.setResult(taskService.taskInfo(appId));
            result.setResult(alarmHistoryService.alarmHistoryInfo(appId));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,-1);
            FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
            result.setResult(executeStatisticService.aggregateByAppId(appId,dateFormat.format(cal)));
        } catch (Exception e) {
            logger.error("获取总览信息失败！！", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }
}
