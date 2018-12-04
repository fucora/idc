package com.iwellmass.idc.app.service;

import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.app.repo.WorkflowEdgeRepository;
import com.iwellmass.idc.app.repo.WorkflowRepository;
import com.iwellmass.idc.app.vo.WorkflowSaveVO;
import com.iwellmass.idc.model.*;
import com.iwellmass.idc.app.vo.WorkflowEnableVO;
import org.jgrapht.Graph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Service
public class WorkflowService {

    @Inject
    private WorkflowRepository workflowRepository;

    @Inject
    private WorkflowEdgeRepository workflowEdgeRepository;

    @Inject
    private TaskRepository taskRepository;

    public WorkflowEdge saveWorkflowEdge(WorkflowEdge workflowEdge) {
        return workflowEdgeRepository.save(workflowEdge);
    }

    public Workflow saveWorkflow(WorkflowSaveVO workflowSaveVO) throws Exception {
        Workflow workflow = workflowSaveVO.getWorkflow();
        List<TaskDependency> taskDependencies = workflowSaveVO.getTaskDependencies();
        if (workflow == null || taskDependencies == null || taskDependencies.size() == 0) {
            throw new Exception("未传入相关数据!");
        }
        // 校验dependency是否成环
        checkAcyclicGraph(taskDependencies);
        // 生成workflowId
        workflow.setGraphId(UUID.randomUUID().toString());
        return workflowRepository.save(workflow);
    }

    public WorkflowEdge itemWorkflowEdge(Integer id) throws Exception {
        return workflowEdgeRepository.findByWorkflowId(id).orElseThrow(() -> new Exception("未查找到该工作流信息"));
    }

    public Workflow item(TaskKey taskKey) throws Exception {
        if (taskKey == null || taskKey.getTaskGroup() == null || taskKey.getTaskId() == null) {
            throw new Exception("传入所有参数");
        }
        return workflowRepository.findByTaskIdAndTaskGroup(taskKey.getTaskId(), taskKey.getTaskGroup())
                .orElseThrow(() -> new Exception("未查找到指定workflow!"));
    }

    @Transactional
    public String enable(WorkflowEnableVO workflowEnableVO) throws Exception {
        // 将task表中的workflowId更新
        if (workflowEnableVO.getTaskDependencies() == null || workflowEnableVO.getTaskDependencies().size() == 0) {
            throw new Exception("未传入需要执行的工作流");
        }
        Workflow workflow = workflowRepository.findByGraphId(workflowEnableVO.getGraphId())
                .orElseThrow(() -> new Exception("未查找到指定工作流"));

        checkAcyclicGraph(workflowEnableVO.getTaskDependencies());
        // 更新task
        Task task = taskRepository.findOne(new TaskKey(workflow.getTaskId(), workflow.getTaskGroup()));
        task.setWorkflowId(workflow.getGraphId());
        taskRepository.save(task);
        // 保存edgs信息
        for (TaskDependency taskDependency : workflowEnableVO.getTaskDependencies()) {
            WorkflowEdge workflowEdge = new WorkflowEdge(workflow.getGraphId(), taskDependency);
            workflowEdgeRepository.save(workflowEdge);
        }
        return "提交成功";
    }


    // 校验dependency是否成环
    private void checkAcyclicGraph(List<TaskDependency> taskDependencies) throws Exception {
        DirectedAcyclicGraph<TaskKey,TaskEdge> directedAcyclicGraph = new DirectedAcyclicGraph(TaskEdge.class);
        for (TaskDependency taskDependency : taskDependencies) {
            try {
                directedAcyclicGraph.addDagEdge(new TaskKey(taskDependency.getSrcTaskId(),taskDependency.getSrcTaskGroup()),new TaskKey(taskDependency.getTaskId(),taskDependency.getTaskGroup()));
            } catch (DirectedAcyclicGraph.CycleFoundException e) {
                throw new Exception("工作流中存在环,请重新编辑");
            }
        }
    }

}
