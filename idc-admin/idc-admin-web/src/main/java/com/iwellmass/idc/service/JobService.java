package com.iwellmass.idc.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.iwellmass.dispatcher.admin.DDCConfiguration;
import com.iwellmass.dispatcher.admin.dao.model.DdcTask;
import com.iwellmass.dispatcher.admin.service.ITaskService;
import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.idc.model.Job;

@Service
public class JobService {
	
	private static final Logger LOGGER =  LoggerFactory.getLogger(JobService.class);

	@Inject
	private ITaskService taskService;
	
	
	@SuppressWarnings("unchecked")
	public void addJob(Job job) {
		
		Date now = new Date();
		
		DdcTask task = new DdcTask();
		
		task.setTaskName(job.getName());
		task.setAppId(DDCConfiguration.DEFAULT_APP);
		task.setAppKey(DDCConfiguration.DEFAULT_APP_KEY);
		task.setClassName(classNameOfTaskType(job.getTaskType()));
		task.setCreateTime(now);
		task.setCreateUser("admin");
		task.setTaskName(job.getTaskId());
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
			taskService.createOrUpdateTask(DDCConfiguration.DEFAULT_APP, task);
		} catch (DDCException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		
		Integer newTaskId = task.getTaskId();
		
		// 这里我们要更新我们的额依赖图
		
		// 获取工作流
		JSONObject workflow = (JSONObject) taskService.getWorkFlow(job.getGroupId());
		LOGGER.debug("获取所属 workflow {}", workflow);
		
		// 添加节点
		List<JSONObject> nodes = (List<JSONObject>) workflow.get("nodeDataArray");
		ArrayList<JSONObject> newNodes = new ArrayList<>(nodes.size() + job.getDependencies().size());
		newNodes.addAll(nodes);
		JSONObject jo = new JSONObject();
		jo.put("loc", "123 456");
		jo.put("figure", "Octagon");
		jo.put("createTime", System.currentTimeMillis());
		jo.put("text", task.getTaskName());
		jo.put("id", newTaskId);
		jo.put("key", newTaskId);
		newNodes.add(jo);
		workflow.put("nodeDataArray", newNodes);
		
		// 处理依赖
		List<JSONObject> deps = (List<JSONObject>) workflow.get("linkDataArray");
		ArrayList<JSONObject> newDeps = new ArrayList<>(deps.size() + job.getDependencies().size());
		newDeps.addAll(deps);
		job.getDependencies().stream().forEach(dep -> {
			JSONObject t = new JSONObject();
			t.put("from", dep.getDependencyId());
			t.put("to", newTaskId);
			newDeps.add(t);
		});
		newNodes.addAll(nodes);
		workflow.put("linkDataArray", newNodes);
		taskService.saveWorkFlow(workflow.toJSONString());
		
	}
	
	
	private static final String classNameOfTaskType(String taskType) {
		return "com.iwellmass.idc.demo.MyTask";
	}
	
}
