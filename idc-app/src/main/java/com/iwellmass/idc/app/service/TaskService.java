package com.iwellmass.idc.app.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.app.mapper.TaskMapper;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.app.vo.SimpleTaskVO;
import com.iwellmass.idc.app.vo.TaskQueryVO;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.model.Workflow;
import com.iwellmass.idc.model.WorkflowEdge;
import com.iwellmass.idc.quartz.IDCPlugin;

@Service
public class TaskService {

    @Inject
    TaskRepository taskRepository;

    @Inject
    TaskMapper taskMapper;

    @Inject
    private IDCPlugin idcPlugin;

    @Inject
    private WorkflowService workflowService;


    @Transactional
    public void add(Task task) {
        try {
        	// 设置默认值
            if (task.getTaskType() == TaskType.WORKFLOW) {
                task.setTaskGroup("idc");
                task.setContentType("workflow");
                task.setWorkflowId(task.getTaskGroup() + "-" + task.getTaskId());
            }
        	
            Task check = taskRepository.findOne(task.getTaskKey());
            
            Assert.isTrue(check == null, "任务 %s 已存在", task.getTaskKey());
          
            // 排序字段
            task.setUpdatetime(LocalDateTime.now());
            
            taskRepository.save(task);
            idcPlugin.refresh(task);
        } catch (SchedulerException e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    @Transactional
    public void update(Task task) {
        Task oldTask = taskRepository.findOne(task.getTaskKey());
        
        if (oldTask == null) {
        	oldTask = task;
        } else {
        	oldTask.setContentType(task.getContentType());
        	oldTask.setTaskName(task.getTaskName());
        	oldTask.setDescription(task.getDescription());
            // 子任务重新注册时我们需要刷新他的参数
            if (task.getTaskType() == TaskType.NODE_TASK) {
            	oldTask.setParameter(task.getParameter());
            }
        }
        
        oldTask.setUpdatetime(LocalDateTime.now());
        
        taskRepository.save(oldTask);
        try {
            idcPlugin.refresh(task);
        } catch (SchedulerException e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    public Task getTask(TaskKey taskKey) {
        return taskRepository.findOne(taskKey);
    }

    public List<Task> getTasksByType(TaskType taskType) {
        Sort sort = new Sort(Direction.DESC, "updatetime");
        return taskRepository.findByTaskType(taskType, sort);
    }

    public PageData<Task> queryTask(TaskQueryVO taskQuery, Pager pager) {

        PageRequest pageable = new PageRequest(pager.getPage(), pager.getLimit(), Direction.DESC, "updatetime");

        Specification<Task> spec = taskQuery == null ? null : SpecificationBuilder.toSpecification(taskQuery);

        Page<Task> ret = taskRepository.findAll(spec, pageable);

        PageData<Task> task = new PageData<>((int) ret.getTotalElements(), ret.getContent());
        return task;
    }

    @Transactional
    public Task modifyGraph(Task task) {
        Assert.isTrue(null != task.getTaskId(), "未传入taskId");
        Assert.isTrue(null != task.getTaskGroup(), "未传入taskGroup");
        
        // 检查是否存在该task
        Task oldTask = taskRepository.findOne(task.getTaskKey());
        if (oldTask == null) {
            throw new AppException("未查找到该taskKey对应的task信息");
        }
        
        List<WorkflowEdge> edges = IDCUtils.parseWorkflowEdge(task.getGraph());
        for (WorkflowEdge we : edges) {
            we.setParentTaskKey(task.getTaskKey()); // 刷新 parentTaskKey
        }
        
        // 刷新 version
        Workflow workflow = new Workflow();
        workflow.setTaskId(task.getTaskId());
        workflow.setTaskGroup(task.getTaskGroup());
        workflow.setGraph(task.getGraph());
        // 工作流是否已改变
        workflowService.saveWorkflow(workflow);

    	// 更新刷新时间
    	oldTask.setUpdatetime(LocalDateTime.now());
    	// 更新工作流的画图数据
    	oldTask.setGraph(task.getGraph());
    	oldTask.setWorkflowId(workflow.getWorkflowId());
    	return taskRepository.save(oldTask);
    }

    /**
     * 查找Node_task 下的parameter
     *
     * @param taskKey
     * @return
     */
    public List<ExecParam> getParam(TaskKey taskKey) {
        Task task = taskRepository.findOne(taskKey);
        if (task == null) {
            throw new AppException("未找到指定task任务");
        }
        return task.getParameter();
    }

    /**
     * 查询 Task 声明的所有参数信息
     */
    public List<SimpleTaskVO> getParams(TaskKey taskKey) {
        // 查询满足要求的Task
        Task task = taskRepository.findOne(taskKey);
        if (task == null) {
            throw new AppException("未查找到指定task:" + taskKey);
        }
        if (task.getTaskType().equals(TaskType.WORKFLOW)) {
            //  工作流任务
            return taskRepository.findAllSubTask(taskKey.getTaskId(), taskKey.getTaskGroup()).stream().map(SimpleTaskVO::new).collect(Collectors.toList());
        } else {
            // NODE_TASK
            return Collections.singletonList(new SimpleTaskVO(task));
        }
    }

	public void validate(TaskKey taskKey) {
		Task task = taskRepository.findOne(taskKey);
		if (task.getTaskType() == TaskType.WORKFLOW) {
			Workflow workflow = workflowService.findOne(task.getWorkflowId());
			Assert.isTrue(workflow != null, "未配置工作流");
		}
	}

}
