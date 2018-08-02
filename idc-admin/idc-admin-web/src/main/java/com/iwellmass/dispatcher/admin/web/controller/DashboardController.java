package com.iwellmass.dispatcher.admin.web.controller;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.dispatcher.admin.service.IAlarmHistoryService;
import com.iwellmass.dispatcher.admin.service.IExecuteStatisticService;
import com.iwellmass.dispatcher.admin.service.INodeService;
import com.iwellmass.dispatcher.admin.service.ITaskService;
import com.iwellmass.dispatcher.admin.service.domain.DataResult;

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
    public DataResult dashboard(int appId) {
        DataResult result = new DataResult();
        try {
            result.addAttribute("node",nodeService.nodeInfo(appId));
            result.addAttribute("task",taskService.taskInfo(appId));
            result.addAttribute("alarm",alarmHistoryService.alarmHistoryInfo(appId));
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE,-1);
            FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
            result.addAttribute("executeStatistic",executeStatisticService.aggregateByAppId(appId,dateFormat.format(cal)));
        } catch (Exception e) {
            logger.error("获取总览信息失败！！", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }
}
