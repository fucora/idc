package com.iwellmass.dispatcher.admin.web.controller;

import static com.iwellmass.dispatcher.admin.web.ResultAdapter.asTableDataResult;
import com.iwellmass.dispatcher.admin.dao.model.DdcSubtaskExecuteHistoryEx;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskExecuteHistoryEx;
import com.iwellmass.dispatcher.admin.service.ITaskExecuteHistory;
import com.iwellmass.dispatcher.admin.service.domain.DataResult;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

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
    public TableDataResult taskHistoryTable(@RequestBody DdcTaskExecuteHistoryEx history) {

//    	MethodInfo methodInfo = Monitor.methodStart("com.dmall.dispatcher.admin.web.controller.TaskHistoryController.taskHistoryTable");
        TableDataResult result = new TableDataResult();
        try {
            result = asTableDataResult(taskExecuteHistory.taskHistoryTable(history.getTask().getAppId(), history));
        } catch (Exception e) {
            logger.error("获取任务执行历史列表数据失败！", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
//            Monitor.methodFail(methodInfo);
        } finally {
//        	Monitor.methodFinish(methodInfo);
        }
        return result;
    }

    @RequestMapping(value = "subTaskHistoryTable", method = RequestMethod.POST)
    @ResponseBody
    public TableDataResult subTaskHistoryTable(@RequestBody DdcSubtaskExecuteHistoryEx history) {
    	
//    	MethodInfo methodInfo = Monitor.methodStart("com.dmall.dispatcher.admin.web.controller.TaskHistoryController.subTaskHistoryTable");
        TableDataResult result = new TableDataResult();
        try {
            result = asTableDataResult(taskExecuteHistory.subTaskHistoryTable(history.getTask().getAppId(), history));
        } catch (Exception e) {
            logger.error("获取子任务执行历史列表数据失败！", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
//            Monitor.methodFail(methodInfo);
        } finally {
//        	Monitor.methodFinish(methodInfo);
        }
        return result;
    }
}
