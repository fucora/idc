package com.iwellmass.dispatcher.admin.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatistic;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatisticEx;
import com.iwellmass.dispatcher.admin.service.ITaskStatisticService;
import com.iwellmass.dispatcher.common.constants.Constants;

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
    public ServiceResult<PageData<DdcTaskStatistic>> taskStatisticTable(@RequestBody DdcTaskStatisticEx taskStatisticEx) {

    	if (taskStatisticEx.getTask().getTaskType() == Constants.TASK_TYPE_SUBTASK) {
    		return ServiceResult.success(taskStatisticService.subTaskStatisticTable(taskStatisticEx.getTask().getAppId(), taskStatisticEx));
    	} else {
    		return ServiceResult.success(taskStatisticService.taskStatisticTable(taskStatisticEx.getTask().getAppId(), taskStatisticEx));
    	}
    }
}
