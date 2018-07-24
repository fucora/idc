package com.iwellmass.dispatcher.admin.web.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatisticEx;
import com.iwellmass.dispatcher.admin.service.ITaskStatisticService;
import com.iwellmass.dispatcher.common.constants.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xkwu on 2016/5/13.
 */
@RequestMapping("taskStatistic")
@Controller
public class TaskStatisticController {
    private static Logger logger = LoggerFactory.getLogger(TaskStatisticController.class);

    @Autowired
    private ITaskStatisticService taskStatisticService;

    @RequestMapping(value = "taskStatisticTable")
    @ResponseBody
    public ServiceResult taskStatisticTable(@RequestBody DdcTaskStatisticEx taskStatisticEx) {


        ServiceResult result = new ServiceResult();
        try {
            if (taskStatisticEx.getTask().getTaskType() == Constants.TASK_TYPE_SUBTASK) {
                result = taskStatisticService.subTaskStatisticTable(taskStatisticEx.getTask().getAppId(), taskStatisticEx);
            } else {
                result = taskStatisticService.taskStatisticTable(taskStatisticEx.getTask().getAppId(), taskStatisticEx);
            }
        } catch (Exception e) {
            logger.error("获取任务统计列表数据失败！", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        } finally {
        }
        return result;
    }
}
