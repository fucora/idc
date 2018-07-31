package com.iwellmass.dispatcher.admin.web.controller.admin;

import static com.iwellmass.dispatcher.admin.web.ResultAdapter.asTableDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.dispatcher.admin.service.IRunningTaskService;
import com.iwellmass.dispatcher.admin.service.domain.DataResult;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

/**
 * Created by xkwu on 2016/11/1.
 */
@Controller
@RequestMapping("/admin/runningTaskManage")
public class RunningTaskManageController {
    private static Logger logger = LoggerFactory.getLogger(RunningTaskManageController.class);

    @Autowired
    private IRunningTaskService iRunningTaskService;

    @RequestMapping(value = "/listRunningTaskTable", method = RequestMethod.POST)
    @ResponseBody
    public TableDataResult listRunningTaskTable() {
        TableDataResult result = new TableDataResult();
        try {
            result = asTableDataResult(iRunningTaskService.runningTaskTable());
        } catch (Exception e) {
            logger.error("获取正在运行任务列表失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/deleteRunningTask", method = RequestMethod.POST)
    @ResponseBody
    public TableDataResult deleteRunningTask(Long id) {
        TableDataResult result = new TableDataResult();
        try {
            iRunningTaskService.deleteRunningTask(id);
        } catch (Exception e) {
            logger.error("删除正在运行任务失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }
}
