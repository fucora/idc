package com.iwellmass.dispatcher.admin.web.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.model.DdcRunningTask;
import com.iwellmass.dispatcher.admin.service.IRunningTaskService;

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
    public ServiceResult<PageData<DdcRunningTask>> listRunningTaskTable() {
    	PageData<DdcRunningTask> result = iRunningTaskService.runningTaskTable();
    	return ServiceResult.success(result);
    }

    @RequestMapping(value = "/deleteRunningTask", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult deleteRunningTask(Long id) {
        ServiceResult result = new ServiceResult();
        try {
            iRunningTaskService.deleteRunningTask(id);
        } catch (Exception e) {
            logger.error("删除正在运行任务失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }
}
