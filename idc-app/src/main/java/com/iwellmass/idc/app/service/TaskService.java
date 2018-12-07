package com.iwellmass.idc.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.app.mapper.TaskMapper;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.app.vo.TaskQueryVO;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

@Service
public class TaskService {

    @Inject
    TaskRepository taskRepository;

    @Inject
    TaskMapper taskMapper;

    @Transactional
    public void saveTask(Task task) {
        Task oldTask = taskRepository.findOne(task.getTaskKey());
        if (oldTask != null) {
        	oldTask.setTaskName(task.getTaskName());
        	oldTask.setDescription(task.getDescription());
        	oldTask.setUpdatetime(LocalDateTime.now());
        	taskRepository.save(oldTask);
        } else {
        	taskRepository.save(task);
        }
    }

    public Task getTask(TaskKey taskKey) {
        return taskRepository.findOne(taskKey);
    }

    public List<Task> getTasksByType(TaskType taskType) {
        return taskRepository.findByTaskType(taskType);
    }

    public PageData<Task> queryTask(TaskQueryVO taskQuery, Pager pager) {

        PageRequest pageable = new PageRequest(pager.getPage(), pager.getLimit(), Direction.DESC, "updatetime");

        Specification<Task> spec = taskQuery == null ? null : SpecificationBuilder.toSpecification(taskQuery);

        Page<Task> ret = taskRepository.findAll(spec, pageable);

        PageData<Task> task = new PageData<>((int) ret.getTotalElements(), ret.getContent());
        return task;
    }

    public Task modifyGraph(Task task) throws Exception {
        Assert.notNull(task.getTaskId(), "未传入taskId");
        Assert.notNull(task.getTaskGroup(), "未传入taskGroup");
        // 检查是否存在该task
        Task oldTask = taskRepository.findOne(task.getTaskKey());
        if (oldTask == null) {
            throw new Exception("未查找到该taskKey对应的task信息");
        }
        IDCUtils.parseWorkflowEdge(task.getGraph());
        // 更新刷新时间
        oldTask.setUpdatetime(LocalDateTime.now());
        // 刷新workflowId
        oldTask.setWorkflowId(UUID.randomUUID().toString());
        // 更新工作流的画图数据
        oldTask.setGraph(task.getGraph());
        return taskRepository.save(oldTask);
    }

}
