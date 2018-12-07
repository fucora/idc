package com.iwellmass.idc.app.service;

import java.time.LocalDateTime;
import java.util.*;

import javax.inject.Inject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iwellmass.idc.model.*;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.mapper.TaskMapper;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.app.vo.TaskQueryVO;
import org.springframework.util.Assert;

@Service
public class TaskService {

    @Inject
    TaskRepository taskRepository;

    @Inject
    TaskMapper taskMapper;

    public void saveTask(Task task) {
        Task oldTask = taskRepository.findOne(task.getTaskKey());
        oldTask.setTaskName(task.getTaskName());
        oldTask.setDescription(task.getDescription());
        oldTask.setUpdatetime(LocalDateTime.now());
        taskRepository.save(task);
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
        // 格式化graph
        List<TaskDependency> taskDependencies = formatGraph(task.getGraph());
        // 校验graph是否正确,检查环
        DirectedAcyclicGraph<TaskKey, TaskEdge> directedAcyclicGraph = checkAcyclicGraph(taskDependencies);
        // 校验graph是否正确,检查孤立点
        checkIsolatedPoints(directedAcyclicGraph);

        // 更新刷新时间
        oldTask.setUpdatetime(LocalDateTime.now());
        // 刷新workflowId
        oldTask.setWorkflowId(UUID.randomUUID().toString());
        // 更新工作流的画图数据
        oldTask.setGraph(task.getGraph());
        return taskRepository.save(oldTask);
    }

    // 格式化graph
    private List<TaskDependency> formatGraph(String graph) throws DirectedAcyclicGraph.CycleFoundException {
        JSONObject jsonObject = JSON.parseObject(graph);
        List<TaskDependency> taskDependencies = new ArrayList<>();
        DirectedAcyclicGraph<TaskKey, TaskEdge> directedAcyclicGraph = new DirectedAcyclicGraph(TaskEdge.class);
        for (JSONObject graphJsonObject : jsonObject.getJSONArray("edges").toJavaList(JSONObject.class)) {
            JSONObject sourceJsonObject = (JSONObject) graphJsonObject.get("source");
            JSONObject targetJsonObject = (JSONObject) graphJsonObject.get("target");
            TaskKey srcTaskKey = new TaskKey();
            TaskKey targetTaskKey = new TaskKey();
            TaskDependency taskDependency = new TaskDependency();
            if (sourceJsonObject != null) {
                srcTaskKey = new TaskKey(sourceJsonObject.getString("taskId"),sourceJsonObject.getString("taskGroup"));
                directedAcyclicGraph.addVertex(srcTaskKey);
                taskDependency.setSrcTaskKey(srcTaskKey);
            }
            if (targetJsonObject != null) {
                targetTaskKey = new TaskKey(targetJsonObject.getString("taskId"),targetJsonObject.getString("taskGroup"));
                directedAcyclicGraph.addVertex(targetTaskKey);
                taskDependency.setTargetTaskKey(targetTaskKey);
            }
            if (sourceJsonObject != null && targetJsonObject != null) {
                directedAcyclicGraph.addDagEdge(srcTaskKey,targetTaskKey);
            }
            if (!(sourceJsonObject == null && targetJsonObject == null)) {
                taskDependencies.add(taskDependency);
            }
        }
        return taskDependencies;
    }

    // 校验dependency是否成环
    private DirectedAcyclicGraph<TaskKey, TaskEdge> checkAcyclicGraph(List<TaskDependency> taskDependencies) throws Exception {
        DirectedAcyclicGraph<TaskKey, TaskEdge> directedAcyclicGraph = new DirectedAcyclicGraph(TaskEdge.class);
        for (TaskDependency taskDependency : taskDependencies) {
            TaskKey srcTaskKey = new TaskKey(taskDependency.getSrcTaskId(), taskDependency.getSrcTaskGroup());
            TaskKey taskKey = new TaskKey(taskDependency.getTaskId(), taskDependency.getTaskGroup());
            if (srcTaskKey.getTaskId() != null && srcTaskKey.getTaskGroup() != null) {
                directedAcyclicGraph.addVertex(srcTaskKey);
            }
            if (taskKey.getTaskId() != null && taskKey.getTaskGroup() != null) {
                directedAcyclicGraph.addVertex(taskKey);
            }
            if (srcTaskKey.getTaskId() != null && srcTaskKey.getTaskGroup() != null && taskKey.getTaskId() != null && taskKey.getTaskGroup() != null) {
                try {
                    directedAcyclicGraph.addDagEdge(srcTaskKey, taskKey);
                } catch (DirectedAcyclicGraph.CycleFoundException e) {
                    throw new Exception("工作流中存在环,请重新编辑");
                }
            }
        }
        return directedAcyclicGraph;
    }

    // 检查孤立点
    private void checkIsolatedPoints(DirectedAcyclicGraph<TaskKey, TaskEdge> directedAcyclicGraph) throws Exception {
        Iterator<TaskKey> iterator = directedAcyclicGraph.vertexSet().iterator();
        while (iterator.hasNext()) {
            TaskKey taskKey = iterator.next();
            if (directedAcyclicGraph.inDegreeOf(taskKey) == 0 && directedAcyclicGraph.outDegreeOf(taskKey) == 0) {
                throw new Exception("工作流中存在孤立任务,请重新编辑");
            }
        }
    }


}
