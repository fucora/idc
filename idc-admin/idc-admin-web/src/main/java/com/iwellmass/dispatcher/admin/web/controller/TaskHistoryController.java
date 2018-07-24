package com.iwellmass.dispatcher.admin.web.controller;

import com.iwellmass.common.ServiceResult;
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
    public ServiceResult taskHistoryTable(@RequestBody DdcTaskExecuteHistoryEx history) {

//    	MethodInfo methodInfo = Monitor.methodStart("com.dmall.dispatcher.admin.web.controller.TaskHistoryController.taskHistoryTable");
        ServiceResult result = new ServiceResult();
        try {
            result = taskExecuteHistory.taskHistoryTable(history.getTask().getAppId(), history);
        } catch (Exception e) {
            logger.error("获取任务执行历史列表数据失败！", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
//            Monitor.methodFail(methodInfo);
        } finally {
//        	Monitor.methodFinish(methodInfo);
        }
        return result;
    }

    @RequestMapping(value = "subTaskHistoryTable", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult subTaskHistoryTable(@RequestBody DdcSubtaskExecuteHistoryEx history) {
    	
//    	MethodInfo methodInfo = Monitor.methodStart("com.dmall.dispatcher.admin.web.controller.TaskHistoryController.subTaskHistoryTable");
        ServiceResult result = new ServiceResult();
        try {
            result = taskExecuteHistory.subTaskHistoryTable(history.getTask().getAppId(), history);
        } catch (Exception e) {
            logger.error("获取子任务执行历史列表数据失败！", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
//            Monitor.methodFail(methodInfo);
        } finally {
//        	Monitor.methodFinish(methodInfo);
        }
        return result;
    }
}
