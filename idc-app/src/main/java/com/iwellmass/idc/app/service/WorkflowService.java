package com.iwellmass.idc.app.service;

import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.app.repo.WorkflowEdgeRepository;
import com.iwellmass.idc.app.repo.WorkflowRepository;
import com.iwellmass.idc.model.TaskDependency;
import com.iwellmass.idc.app.vo.WorkflowEnableVO;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.Workflow;
import com.iwellmass.idc.model.WorkflowEdge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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

    public Workflow saveWorkflow(Workflow workflow) {
        // 生成workflowId
        workflow.setWorkflowId(UUID.randomUUID().toString());
        return workflowRepository.save(workflow);
    }

    public WorkflowEdge itemWorkflowEdge(Integer id) throws Exception {
        return workflowEdgeRepository.findByWorkflowId(id).orElseThrow(() -> new Exception("未查找到该工作流信息"));
    }

    public Workflow itemWorkflow(Integer id) throws Exception {
        return workflowRepository.findByWorkflowId(id).orElseThrow(() -> new Exception("未查找到该工作流基本信息"));
    }

    public Workflow item(TaskKey taskKey) throws Exception {
        if (taskKey == null || taskKey.getTaskGroup() == null || taskKey.getTaskId() == null) {
            throw new Exception("传入所有参数");
        }
        return workflowRepository.findByTaskIdAndTaskGroup(taskKey.getTaskId(), taskKey.getTaskGroup()).orElseThrow(() -> new Exception("未查找到指定workflow!"));
    }

    @Transactional
    public String enable(WorkflowEnableVO workflowEnableVO) throws Exception {
        // 将task表中的workflowId更新
        if (workflowEnableVO.getTaskDependencies() == null || workflowEnableVO.getTaskDependencies().size() == 0) {
            throw new Exception("未传入需要执行的工作流");
        }
        Task task = taskRepository.findOne(workflowEnableVO.getTaskKey());
        if (task == null) {
            throw new Exception("未查找到指定task");
        }
        Workflow workflow = workflowRepository.findOne(workflowEnableVO.getTaskKey());
        if (workflow == null) {
            throw new Exception("未查找到指定工作流");
        }
        // 更新task
        task.setWorkflowId(workflow.getWorkflowId());
        taskRepository.save(task);
        // 保存edgs信息
        for (TaskDependency taskDependency : workflowEnableVO.getTaskDependencies()) {
            WorkflowEdge workflowEdge = new WorkflowEdge(workflow.getWorkflowId(), taskDependency);
            workflowEdgeRepository.save(workflowEdge);
        }
        return "提交成功";
    }


}
