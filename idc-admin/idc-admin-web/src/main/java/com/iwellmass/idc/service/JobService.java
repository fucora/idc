package com.iwellmass.idc.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.dispatcher.admin.DDCConfiguration;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskWorkflowMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcTask;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskExample;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskWorkflowWithBLOBs;
import com.iwellmass.dispatcher.admin.service.ITaskService;
import com.iwellmass.dispatcher.common.DDCContext;
import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.dispatcher.thrift.bvo.TaskTypeHelper;
import com.iwellmass.idc.mapper.IdcTaskMapper;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobQuery;

@Service
public class JobService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

	@Inject
	private ITaskService taskService;

	@Inject
	private DdcTaskWorkflowMapper ddcTaskWorkflowMapper;

	@Inject
	private DdcTaskMapper ddcTaskMapper;

	@Inject
	private IdcTaskMapper idcTaskMapper;

	public void addJob(Job job) {

		Date now = new Date();

		DdcTask task = new DdcTask();

		task.setTaskName(job.getJobName());
		task.setAppId(DDCContext.DEFAULT_APP);
		task.setAppKey(DDCContext.DEFAULT_APP_KEY);
		task.setClassName(TaskTypeHelper.classNameOf(job.getContentType()));
		task.setCreateTime(now);
		task.setCreateUser(job.getAssignee());
		task.setCron(job.getScheduleProperties().toCronExpr(job.getScheduleType()));
		task.setOwner(job.getAssignee());
		task.setTimeout(60L);

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
		task.setWorkflowId(job.getWorkflowId());

		try {
			taskService.createOrUpdateTask(DDCContext.DEFAULT_APP, task);
			job.setId(task.getTaskId());
			if (job.hasDependencies()) {
				updateDependency(job);
			}
		} catch (DDCException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public List<Job> getWorkflowJob(Integer taskId) {

		DdcTask task = ddcTaskMapper.selectByPrimaryKey(taskId);

		DdcTaskWorkflowWithBLOBs workflow = ddcTaskWorkflowMapper.selectByPrimaryKey(task.getWorkflowId());

		if (workflow == null) {
			return Collections.emptyList();
		}

		JSONObject jo = (JSONObject) JSON.parse(workflow.getJson());

		JSONArray nodeDataArray = jo.getJSONArray("nodeDataArray");

		List<Integer> list = nodeDataArray.stream().map(t -> ((JSONObject) t).getInteger("id")).filter(id -> id > 0)
				.collect(Collectors.toList());

		DdcTaskExample example = new DdcTaskExample();
		example.createCriteria().andTaskIdIn(list);

		return ddcTaskMapper.selectByExample(example).stream().map(this::newJob).collect(Collectors.toList());
	}

	private void updateDependency(Job job) {

		Integer newTaskId = job.getId();

		// 这里我们要更新我们的依赖图

		// 获取工作流
		JSONObject workflow = (JSONObject) taskService.getWorkFlow(job.getWorkflowId());

		if (workflow == null) {
			workflow = new JSONObject();

			// 初始化 workflow
			workflow.put("nodeKeyProperty", "id");
			workflow.put("taskId", job.getWorkflowId());
			workflow.put("nodeDataArray", new JSONArray());
			workflow.put("linkDataArray", new JSONArray());

			// 初始化开始、结束节点
			JSONObject startNode = new JSONObject();
			startNode.put("id", -1);
			startNode.put("key", -1);
			startNode.put("category", "Start");
			startNode.put("loc", "-932");
			startNode.put("text", "开始");
			workflow.getJSONArray("nodeDataArray").add(startNode);
			JSONObject endNode = new JSONObject();
			endNode.put("id", -2);
			endNode.put("key", -2);
			endNode.put("category", "End");
			endNode.put("loc", "-281");
			endNode.put("text", "结束");
			workflow.getJSONArray("nodeDataArray").add(endNode);
		}

		LOGGER.debug("获取所属 workflow {}", workflow);

		// 添加节点
		JSONObject newNode = new JSONObject();
		newNode.put("id", newTaskId);
		newNode.put("key", newTaskId);
		newNode.put("createTime", System.currentTimeMillis());
		newNode.put("loc", "123 456");
		newNode.put("figure", "Octagon");
		newNode.put("text", job.getJobName());
		workflow.getJSONArray("nodeDataArray").add(newNode);

		// 处理依赖，有下游
		if (job.hasDependencies()) {
			JSONArray array = workflow.getJSONArray("linkDataArray");
			job.getDependencies().stream().forEach(dep -> {
				JSONObject t = new JSONObject();
				t.put("from", dep.getDependencyId());
				t.put("to", newTaskId);
				array.add(t);
			});
		}
		// 无下游，从开始到结束
		else {
			JSONArray array = workflow.getJSONArray("linkDataArray");
			// 本节点 开始 -> 本节点
			JSONObject t = new JSONObject();
			t.put("from", -1);
			t.put("to", newTaskId);
			array.add(t);
			// 本节点 -> 结束
			JSONObject t2 = new JSONObject();
			t2.put("from", newTaskId);
			t2.put("to", -2);
			array.add(t2);
		}
		taskService.saveWorkFlow(workflow.toJSONString());
	}

	public void lockStatus(int taskId) throws DDCException {
		taskService.disableTask(DDCConfiguration.DEFAULT_APP, taskId);
	}

	private Job newJob(DdcTask task) {
		Job job = new Job();
		job.setJobName(task.getTaskName());
		job.setId(task.getTaskId());
		job.setDescription(task.getDescription());
		job.setAssignee(task.getOwner());
		job.setWorkflowId(task.getWorkflowId());
		job.setCreateTime(new Timestamp(task.getCreateTime().getTime()));
		return job;
	}

	/**
	 * 通过条件查询job
	 * 
	 * @param query
	 * @return
	 */
	public PageData<Job> findTasksByCondition(JobQuery query, Pager pager) {
		Pager pager1 = new Pager();
		pager1.setPage(pager.getTo());
		pager1.setLimit(pager.getLimit());
		List<Job> allTasks = idcTaskMapper.findAllTasksByCondition(query);
		List<Job> tasks = idcTaskMapper.findTasksByCondition(query, pager1);
		tasks.forEach(j -> {
			j.setContentType(TaskTypeHelper.contentTypeOf(j.getContentType()));
		});
		return new PageData<Job>(allTasks.size(), tasks);
	}

	public List<Job> findTaskByWorkflowId(Integer id) {
		List<Job> taskByGroupId = idcTaskMapper.findTaskByWorkflowId(id);
		return taskByGroupId;
	}

	public List<JobQuery> getAllAssignee() {
		List<JobQuery> list = new ArrayList<>();
		List<JobQuery> list1 = new ArrayList<>();
		idcTaskMapper.findAllTasks().forEach(i -> {
			JobQuery query = new JobQuery();
			query.setAssignee(i.getAssignee());
			list.add(query);
		});
		for (JobQuery query : list) {
			boolean is = list1.stream().anyMatch(t -> t.getAssignee().equals(query.getAssignee()));
			if (!is) {
				list1.add(query);
			}
		}
		return list1;
	}

	public List<Job> getWorkflowJob() {
		return idcTaskMapper.findAllWorkflowJob();
	}

}
