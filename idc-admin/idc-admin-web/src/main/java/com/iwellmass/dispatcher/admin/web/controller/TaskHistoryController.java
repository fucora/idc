package com.iwellmass.dispatcher.admin.web.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.model.DdcSubtaskExecuteHistoryEx;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskExecuteHistoryEx;
import com.iwellmass.dispatcher.admin.service.ITaskExecuteHistory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xkwu on 2016/5/13.
 */
@Controller
@RequestMapping("taskHistory")
public class TaskHistoryController {
    private static Logger logger = LoggerFactory.getLogger(TaskHistoryController.class);
    @Autowired
    private ITaskExecuteHistory taskExecuteHistory;

    @RequestMapping(value = "taskHistoryTable", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<PageData<DdcTaskExecuteHistoryEx>> taskHistoryTable(@RequestBody DdcTaskExecuteHistoryEx history) {
    	return ServiceResult.success(taskExecuteHistory.taskHistoryTable(history.getTask().getAppId(), history));
    }

    @RequestMapping(value = "subTaskHistoryTable", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult<PageData<DdcSubtaskExecuteHistoryEx>> subTaskHistoryTable(@RequestBody DdcSubtaskExecuteHistoryEx history) {
    	
    	return ServiceResult.success(taskExecuteHistory.subTaskHistoryTable(history.getTask().getAppId(), history));
    }
}
