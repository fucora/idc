package com.iwellmass.idc.service;

import java.util.Date;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iwellmass.dispatcher.admin.DDCConfiguration;
import com.iwellmass.dispatcher.admin.dao.model.DdcTask;
import com.iwellmass.dispatcher.admin.service.ITaskService;
import com.iwellmass.dispatcher.common.DDCContext;
import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.idc.model.Job;

@Service
public class JobService {
	
	private static final Logger LOGGER =  LoggerFactory.getLogger(JobService.class);

	@Inject
	private ITaskService taskService;
	
	
	public void addJob(Job job) {
		
		Date now = new Date();
		
		DdcTask task = new DdcTask();
		
		task.setTaskName(job.getJobName());
		task.setAppId(DDCContext.DEFAULT_APP);
		task.setAppKey(DDCContext.DEFAULT_APP_KEY);
		task.setClassName(classNameOfTaskType(job.getTaskType()));
		task.setCreateTime(now);
		task.setCreateUser("admin");
		task.setTaskName(job.getJobName());
		task.setCron(job.getScheduleProperties().toCronExpr(job.getScheduleType()));
		
		if (job.hasDependencies()) {
			task.setTaskCategoty(Constants.TASK_CATEGORY_WORKFLOW);
			task.setTaskType(Constants.TASK_TYPE_SUBTASK);
		} else {
			task.setTaskCategoty(Constants.TASK_CATEGORY_BASIC);
			task.setTaskType(Constants.TASK_TYPE_CRON);
		}
		task.setTaskStatus(Constants.TASK_STATUS_ENABLED);
//		category == Constants.TASK_CATEGORY_BASIC && type == Constants.TASK_TYPE_SUBTASK
		// 统一使用 workflow 引擎
		task.setTaskCategoty(Constants.TASK_CATEGORY_BASIC);
		task.setTaskType(Constants.TASK_TYPE_SUBTASK);
		
		try {
			taskService.createOrUpdateTask(DDCContext.DEFAULT_APP, task);
		} catch (DDCException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		
		Integer newTaskId = task.getTaskId();
		
		// 这里我们要更新我们的额依赖图
		
		// 获取工作流
		JSONObject workflow = (JSONObject) taskService.getWorkFlow(job.getGroupId());
		LOGGER.debug("获取所属 workflow {}", workflow);
		
		// 添加节点
		JSONArray nodes = (JSONArray) workflow.get("nodeDataArray");
		JSONObject jo = new JSONObject();
		jo.put("loc", "123 456");
		jo.put("figure", "Octagon");
		jo.put("createTime", System.currentTimeMillis());
		jo.put("text", task.getTaskName());
		jo.put("id", newTaskId);
		jo.put("key", newTaskId);
		nodes.add(jo);
		
		// 处理依赖
		JSONArray deps = (JSONArray) workflow.get("linkDataArray");
		job.getDependencies().stream().forEach(dep -> {
			JSONObject t = new JSONObject();
			t.put("from", dep.getDependencyId());
			t.put("to", newTaskId);
			deps.add(t);
		});
		taskService.saveWorkFlow(workflow.toJSONString());
		
	}
	
	
	private static final String classNameOfTaskType(String taskType) {
		return "com.iwellmass.idc.demo.MyTask";
	}
	
}
