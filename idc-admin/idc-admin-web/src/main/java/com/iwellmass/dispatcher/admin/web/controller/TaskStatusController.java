package com.iwellmass.dispatcher.admin.web.controller;

import static com.iwellmass.dispatcher.admin.web.ResultAdapter.asTableDataResult;
import com.iwellmass.dispatcher.admin.dao.Page;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskExecuteStatus;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;
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
    public TableDataResult taskStatusTable(DdcTaskExecuteStatus status, Page page) {
        return asTableDataResult(statusService.taskStatusTable(status, page));
    }
}
