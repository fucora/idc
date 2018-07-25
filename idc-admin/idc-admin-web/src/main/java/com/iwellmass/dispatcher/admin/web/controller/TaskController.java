package com.iwellmass.dispatcher.admin.web.controller;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.Pager;
import com.iwellmass.dispatcher.admin.dao.model.DdcTask;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskUpdateHistory;
import com.iwellmass.dispatcher.admin.service.IExecuteStatisticService;
import com.iwellmass.dispatcher.admin.service.ITaskService;
import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.dispatcher.common.utils.DateUtils;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("task")
public class TaskController {

	private static Logger logger = LoggerFactory.getLogger(TaskController.class);

	@Autowired
	private ITaskService taskService;

	@Autowired
	private IExecuteStatisticService executeStatisticService;

	@PostMapping
	@ApiOperation("新增调度任务")
	public ServiceResult createOrUpdateTask(DdcTask task) {

		ServiceResult result = new ServiceResult();
		try {

			// 如果是简单任务校验参数配置
			if (task.getTaskCategoty() == Constants.TASK_CATEGORY_BASIC
					&& task.getTaskType() == Constants.TASK_TYPE_SIMPLE) {

				String startTimeStr = task.getSimpleStartTime();
				Date startTime = null;

				if (StringUtils.isBlank(startTimeStr)) {
					throw new DDCException("执行开始时间不能为空！");
				}

				try {
					startTime = DateUtils.parseDate(startTimeStr, DateUtils.FORMAT_DATETIME);
				} catch (Exception e) {
					throw new DDCException("执行开始时间{%s}格式错误！", startTimeStr);
				}

				if (startTime.before(new Date())) {
					throw new DDCException("执行开始时间不能小于当前时间！");
				}

				String endTimeStr = task.getSimpleEndTime();
				Date endTime = null;
				try {
					if (StringUtils.isNotBlank(endTimeStr)) {
						endTime = DateUtils.parseDate(endTimeStr, DateUtils.FORMAT_DATETIME);
					}
				} catch (Exception e) {
					throw new DDCException("执行结束时间{%s}格式错误！", endTimeStr);
				}
				if (endTime != null && endTime.before(startTime)) {
					throw new DDCException("执行结束时间不能小于执行开始时间！");
				}

				if (task.getSimpleRepeatCount() != 1
						&& (task.getSimpleRepeatInterval() == null || task.getSimpleRepeatInterval().intValue() == 0)) {
					throw new DDCException("执行间隔时间不能为空！");
				}
			}

			taskService.createOrUpdateTask(task.getAppId(), task);
		} catch (Exception e) {
			logger.error("创建或者更新任务失败！", e);
			result.setState(ServiceResult.STATE_APP_EXCEPTION);
			result.setError(e.getMessage());
		} finally {
		}
		return result;
	}

	@RequestMapping(value = "taskTable", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult<PageData<DdcTask>> taskTable(DdcTask task, Pager page) {
		return ServiceResult.success(taskService.taskTable(task.getAppId(), task, page));
	}

	@RequestMapping(value = "modifyTaskStatus", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult modifyTaskStatus(int appId, int taskId, boolean enable) {
		ServiceResult result = new ServiceResult();
		try {
			if (enable) {
				taskService.enableTask(appId, taskId);
			} else {
				taskService.disableTask(appId, taskId);
			}
		} catch (Exception e) {
			logger.error("改变任务状态失败！", e);
			result.setState(ServiceResult.STATE_APP_EXCEPTION);
			result.setError(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "executeTask", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult executeTask(int appId, int taskId) {

		ServiceResult result = new ServiceResult();
		try {
			taskService.executeTask(appId, taskId);
		} catch (Exception e) {
			logger.error("执行任务失败！", e);
			result.setState(ServiceResult.STATE_APP_EXCEPTION);
			result.setError(e.getMessage());
		} finally {
		}
		return result;
	}

	@RequestMapping(value = "deleteTask", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult deleteTask(int appId, int taskId) {
		ServiceResult result = new ServiceResult();
		try {
			taskService.deleteTask(appId, taskId);
		} catch (Exception e) {
			if (!(e instanceof DDCException)) {
				logger.error("删除任务失败！", e);
			}
			result.setState(ServiceResult.STATE_APP_EXCEPTION);
			result.setError(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "getWorkFlow", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult getWorkFlow(int appId, int taskId) {
		ServiceResult result = new ServiceResult();
		try {
			result.setResult(taskService.listSubTask(appId));
			result.setResult(taskService.getWorkFlow(taskId));
		} catch (Exception e) {
			logger.error("获取子任务列表失败！", e);
			result.setState(ServiceResult.STATE_APP_EXCEPTION);
			result.setError(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "saveWorkFlow", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult saveWorkFlow(String json) {
		ServiceResult result = new ServiceResult();
		try {
			taskService.saveWorkFlow(json);
		} catch (Exception e) {
			logger.error("获取子任务列表失败！", e);
			result.setState(ServiceResult.STATE_APP_EXCEPTION);
			result.setError(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/aggregate", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult aggregate(int appId, int taskId, String type, int num) {
		ServiceResult result = new ServiceResult();
		try {
			logger.info("任务执行情况汇总：开始！");
			Calendar cal = Calendar.getInstance();
			cal.set(java.util.Calendar.SECOND, 0);
			cal.set(java.util.Calendar.MILLISECOND, 0);
			FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
			if (type.equals("hour")) {
				cal.add(Calendar.HOUR, -num);
				result.setResult(executeStatisticService
						.fiveMinuteAggregateByAppIdAndTaskId(appId, taskId, dateFormat.format(cal)));
			}
			if (type.equals("day")) {
				cal.add(Calendar.DATE, -num);
				result.setResult(executeStatisticService.hourAggregateByAppIdAndTaskId(appId, taskId, dateFormat.format(cal)));
			}
		} catch (Exception e) {
			logger.error("获取汇聚信息失败！！", e);
			result.setState(ServiceResult.STATE_APP_EXCEPTION);
			result.setError(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/taskUpdateHistoryTable", method = RequestMethod.POST)
	@ResponseBody
	public ServiceResult<PageData<DdcTaskUpdateHistory>> taskUpdateHistoryTable(DdcTaskUpdateHistory history, Pager page) {
		return ServiceResult.success(taskService.taskUpdateHistoryTable(history, page));
	}

}
