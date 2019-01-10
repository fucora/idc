package com.iwellmass.idc.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.app.mapper.TaskMapper;
import com.iwellmass.idc.app.model.SimpleTaskVO;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.app.util.Util;
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
        task.setTaskGroup("idc");
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
        
        // 兼容 bad parameter
        if ("null".equalsIgnoreCase(oldTask.getParameter())) {
        	oldTask.setParameter(null);
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
        
        // 更新刷新时间
        oldTask.setUpdatetime(LocalDateTime.now());
        // 更新工作流的画图数据
        oldTask.setGraph(task.getGraph());
        
        // 刷新 version
        Workflow workflow = new Workflow();
        workflow.setTaskId(task.getTaskId());
        workflow.setTaskGroup(task.getTaskGroup());
        workflow.setGraph(task.getGraph());
        // 工作流是否已改变
        workflowService.saveWorkflow(workflow);
        
        oldTask.setWorkflowId(workflow.getWorkflowId());
        
        return taskRepository.save(oldTask);
    }

    /**
     * 查找Node_task 下的parameter
     *
     * @param taskKey
     * @return
     */
    public String getParam(TaskKey taskKey) {
        Task task = taskRepository.findOne(taskKey);
        if (task == null) {
            throw new AppException("未找到指定task任务");
        }
        return task.getParameter();
    }

    /**
     * 查询Workflow 下的子任务下的全部parameter
     */
    public List<SimpleTaskVO> getParams(TaskKey taskKey) {
        // 查询满足要求的Task
        Task task = taskRepository.findOne(taskKey);
        if (task == null) {
            throw new AppException("未查找到指定task:" + taskKey);
        }
        List<SimpleTaskVO> tasks = new ArrayList<>();
        if (task.getTaskType().equals(TaskType.WORKFLOW)) {
            //  工作流任务
            List<Object[]> parentTaskKeyObjects = taskRepository.findSrcTaskKeyByParentTaskKey(taskKey);
            List<TaskKey> taskKeysRepeat = Util.castEntity(parentTaskKeyObjects, TaskKey.class);
            Map<TaskKey, String> taskKeysMap = new HashMap<>();
            taskKeysRepeat.forEach(tk -> taskKeysMap.put(tk, tk.getTaskId() + "." + tk.getTaskGroup()));
            taskKeysMap.forEach((tk, s) -> {
                Task taskTemp = taskRepository.findOne(tk);
                if (taskTemp == null && !tk.equals(WorkflowEdge.END) && !tk.equals(WorkflowEdge.START)) {
                    throw new AppException("未查找到该子任务:" + tk);
                }
                if (!tk.equals(WorkflowEdge.END) && !tk.equals(WorkflowEdge.START)) {
                    tasks.add(new SimpleTaskVO(taskTemp));
                }
            });
        } else {
            // NODE_TASK
            tasks.add(new SimpleTaskVO(task));
        }
        return tasks;
    }

}
