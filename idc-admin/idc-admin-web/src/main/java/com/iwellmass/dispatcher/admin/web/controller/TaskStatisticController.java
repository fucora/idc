package com.iwellmass.dispatcher.admin.web.controller;

import static com.iwellmass.dispatcher.admin.web.ResultAdapter.asTableDataResult;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskStatisticEx;
import com.iwellmass.dispatcher.admin.service.ITaskStatisticService;
import com.iwellmass.dispatcher.admin.service.domain.DataResult;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;
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
    public TableDataResult taskStatisticTable(@RequestBody DdcTaskStatisticEx taskStatisticEx) {


        TableDataResult result = new TableDataResult();
        try {
            if (taskStatisticEx.getTask().getTaskType() == Constants.TASK_TYPE_SUBTASK) {
                result = asTableDataResult(taskStatisticService.subTaskStatisticTable(taskStatisticEx.getTask().getAppId(), taskStatisticEx));
            } else {
                result = asTableDataResult(taskStatisticService.taskStatisticTable(taskStatisticEx.getTask().getAppId(), taskStatisticEx));
            }
        } catch (Exception e) {
            logger.error("获取任务统计列表数据失败！", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        } finally {
        }
        return result;
    }
}
