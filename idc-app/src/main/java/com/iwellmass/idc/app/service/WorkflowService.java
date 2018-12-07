package com.iwellmass.idc.app.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.app.repo.WorkflowEdgeRepository;
import com.iwellmass.idc.app.repo.WorkflowRepository;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.Workflow;
import com.iwellmass.idc.model.WorkflowEdge;

@Service
public class WorkflowService {

    @Inject
    private WorkflowRepository workflowRepository;

    @Inject
    private WorkflowEdgeRepository workflowEdgeRepository;


    @Transactional
    public Workflow saveWorkflow(Workflow workflow) {
    	List<WorkflowEdge> edges = IDCUtils.parseWorkflowEdge(workflow.getGraph());
    	workflowEdgeRepository.save(edges);
        return workflowRepository.save(workflow);
    }


    public Workflow item(TaskKey taskKey) throws Exception {
        if (taskKey == null || taskKey.getTaskGroup() == null || taskKey.getTaskId() == null) {
            throw new Exception("传入所有参数");
        }
        return workflowRepository.findByTaskIdAndTaskGroup(taskKey.getTaskId(), taskKey.getTaskGroup())
                .orElseThrow(() -> new Exception("未查找到指定workflow!"));
    }
}
