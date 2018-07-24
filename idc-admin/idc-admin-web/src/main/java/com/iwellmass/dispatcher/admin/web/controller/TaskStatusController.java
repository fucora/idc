package com.iwellmass.dispatcher.admin.web.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.Pager;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskExecuteStatus;
import com.iwellmass.dispatcher.admin.service.impl.TaskStatusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xkwu on 2016/5/13.
 */
@RequestMapping("taskStatus")
@Controller
public class TaskStatusController {
    @Autowired
    private TaskStatusService statusService;

    @RequestMapping(value = "taskStatusTable",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult taskStatusTable(DdcTaskExecuteStatus status, Pager page) {
        return statusService.taskStatusTable(status, page);
    }
}
