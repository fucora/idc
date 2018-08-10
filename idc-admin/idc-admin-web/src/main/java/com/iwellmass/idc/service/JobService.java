package com.iwellmass.idc.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.dispatcher.admin.DDCConfiguration;
import com.iwellmass.dispatcher.admin.JobInstanceTypeHandler;
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
import com.iwellmass.idc.controller.ComplementRequest;
import com.iwellmass.idc.mapper.IdcTaskMapper;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstanceType;
import com.iwellmass.idc.model.JobQuery;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.TaskType;

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
    
    @Inject
    private Scheduler scheduler;

	public void schedule(Job job) {

        Date now = new Date();

        DdcTask task = new DdcTask();

		task.setTaskName(job.getJobName());
		task.setAppId(DDCContext.DEFAULT_APP);
		task.setAppKey(DDCContext.DEFAULT_APP_KEY);
		task.setClassName(TaskTypeHelper.classNameOf(job.getContentType().toString()));
		task.setCreateTime(now);
		task.setCreateUser(job.getAssignee());
		task.setOwner(job.getAssignee());
		task.setTimeout(60L);
		task.setTaskStatus(Constants.TASK_STATUS_ENABLED);
		task.setConcurrency(Boolean.FALSE);

        JSONObject jo = new JSONObject();
        
        // 调度类型
        if (ScheduleType.MANUAL == job.getScheduleType()) {
        	jo.put("triggerType",  JobInstanceTypeHandler.asDDCTriggerType(JobInstanceType.MANUAL));
        }
        else {
        	task.setCron(job.getScheduleProperties().toCronExpr(job.getScheduleType()));
        	jo.put("triggerType", JobInstanceTypeHandler.asDDCTriggerType(JobInstanceType.CRON));
        }
        
        JSONObject map = new JSONObject();
        map.put("taskId", job.getTaskId());
        
        jo.put("task", map);
        task.setParameters(jo.toJSONString());

		// 单节点任务
		if (job.getTaskType() == TaskType.NODE_TASK) {
			task.setTaskCategoty(Constants.TASK_CATEGORY_BASIC);
			task.setTaskType(Constants.TASK_TYPE_CRON);
		} 
		// 流程主任务
		else if (job.getTaskType() == TaskType.WORKFLOW) {
			// TODO
		} 
		// 工作流子任务
		else if (job.getTaskType() == TaskType.WORKFLOW_TASK) {
			// category == Constants.TASK_CATEGORY_BASIC && type == Constants.TASK_TYPE_SUBTASK
			task.setTaskCategoty(Constants.TASK_CATEGORY_BASIC);
			task.setTaskType(Constants.TASK_TYPE_SUBTASK);
		}
		try {
			taskService.createOrUpdateTask(DDCContext.DEFAULT_APP, task);
			job.setId(task.getTaskId());
			if (job.getTaskType() == TaskType.WORKFLOW_TASK) {
				updateDependency(job.getWorkflowId(), job);
			}
		} catch (DDCException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	
    public void complement(ComplementRequest request) {
    	
    	Integer jobId = request.getJobId();
    	
    	DdcTask ddcTask = ddcTaskMapper.selectByPrimaryKey(jobId);
    	JobKey jobKey = buildJobKey(ddcTask);
    	
    	try {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			
			if (triggers.isEmpty()) {
				throw new AppException("任务未正确提交");
			} else {
				LocalDate start = request.getStart();
				LocalDate end = request.getEnd();
				
				Date startDate = new Date(start.atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
				Date endDate = new Date(end.atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
				
				Trigger trigger = triggers.stream().filter(t ->  t.getKey().getName().matches(
						Constants.TRIGGER_PREFIX + "_ \\d+.+")
				).findFirst().orElseThrow( () -> new AppException(""));
				
				if (trigger == null) {
					throw new AppException("任务未正确提交");
				}
				// 调起来
				Trigger complementTrigger = trigger.getTriggerBuilder()
					.withIdentity(buildComplementTriggerKey(ddcTask))
					.startAt(startDate).endAt(endDate).build();
				complementTrigger.getJobDataMap().put("triggerType", JobInstanceTypeHandler.asDDCTriggerType(JobInstanceType.COMPLEMENT));
				complementTrigger.getJobDataMap().put("user", "admin");
				scheduler.scheduleJob(complementTrigger);
			}
		} catch (Exception e1) {
			throw new AppException("补数失败" + e1.getMessage());
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

        return idcTaskMapper.findWorkflowJob(list);
    }

	private void updateDependency(Integer workflowId, Job subJob) {

		Integer subTaskId = subJob.getId();

        // 这里我们要更新我们的依赖图

		// 获取工作流
		JSONObject workflow = (JSONObject) taskService.getWorkFlow(subJob.getWorkflowId());

        if (workflow == null) {
            workflow = new JSONObject();

			// 初始化 workflow
			workflow.put("nodeKeyProperty", "id");
			workflow.put("taskId", workflowId);
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
		newNode.put("id", subTaskId);
		newNode.put("key", subTaskId);
		newNode.put("createTime", System.currentTimeMillis());
		newNode.put("loc", "123 456");
		newNode.put("figure", "Octagon");
		newNode.put("text", subJob.getJobName());
		workflow.getJSONArray("nodeDataArray").add(newNode);

		// 处理依赖，有下游
		if (subJob.hasDependencies()) {
			JSONArray array = workflow.getJSONArray("linkDataArray");
			subJob.getDependencies().stream().forEach(dep -> {
				JSONObject t = new JSONObject();
				t.put("from", dep.getDependencyId());
				t.put("to", subTaskId);
				array.add(t);
			});
		}
		// 无下游，从开始到结束
		else {
			JSONArray array = workflow.getJSONArray("linkDataArray");
			// 本节点 开始 -> 本节点
			JSONObject t = new JSONObject();
			t.put("from", -1);
			t.put("to", subTaskId);
			array.add(t);
			// 本节点 -> 结束
			JSONObject t2 = new JSONObject();
			t2.put("from", subTaskId);
			t2.put("to", -2);
			array.add(t2);
		}
		taskService.saveWorkFlow(workflow.toJSONString());
	}

    public void lockStatus(int taskId) throws DDCException {
        taskService.disableTask(DDCConfiguration.DEFAULT_APP, taskId);
    }

    public void unlockStatus(int taskId) throws DDCException {
        taskService.enableTask(DDCConfiguration.DEFAULT_APP, taskId);
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
    
    private JobKey buildJobKey(DdcTask task) {

        JobKey jobKey = new JobKey(Constants.JOB_PREFIX + task.getTaskId(), task.getAppKey());
        return jobKey;
    }
    
    private TriggerKey buildComplementTriggerKey(DdcTask task) {
        TriggerKey triggerKey = new TriggerKey(Constants.TRIGGER_PREFIX + "_complement_" + task.getTaskId(), task.getAppKey());
        return triggerKey;
    }


}
