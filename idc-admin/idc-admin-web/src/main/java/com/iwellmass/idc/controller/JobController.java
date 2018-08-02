package com.iwellmass.idc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.IDCPager;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.service.JobService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job")
public class JobController {

	@Autowired
	private JobService jobService;

	@PostMapping
	@ApiOperation("新增调度任务")
	public ServiceResult<String> addJob(@RequestBody Job job){
		jobService.addJob(job);
		return ServiceResult.success("success");
	}

	@PostMapping(value = "/query")
	@ApiOperation("查询调度任务")
	public ServiceResult<PageData<Job>> queryJobs(@RequestBody JobQueryController query, IDCPager page) {
		return ServiceResult.failure("not supported yet");
		//return ServiceResult.success(taskService.taskTable(task.getAppId(), task, page));
	}

	@RequestMapping(value = "/{id}/lock-status", method = RequestMethod.POST)
	@ApiOperation("冻结/恢复 Job")
	public ServiceResult<String> lock(int appId, int taskId, boolean enable) {
		return ServiceResult.failure("not supported yet.");
	}

	/*@RequestMapping(value = "executeTask", method = RequestMethod.POST)
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
	public ServiceResult<PageData<DdcTaskUpdateHistory>> taskUpdateHistoryTable(DdcTaskUpdateHistory history, IDCPager page) {
		return ServiceResult.success(taskService.taskUpdateHistoryTable(history, page));
	}*/

}
