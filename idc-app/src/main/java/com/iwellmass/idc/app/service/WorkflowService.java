package com.iwellmass.idc.app.service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.app.repo.WorkflowEdgeRepository;
import com.iwellmass.idc.app.repo.WorkflowRepository;
import com.iwellmass.idc.model.Workflow;
import com.iwellmass.idc.model.WorkflowEdge;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;

@Service
public class WorkflowService {

    @Inject
    private WorkflowRepository workflowRepository;
    @Inject
    private WorkflowEdgeRepository workflowEdgeRepository;

    public WorkflowEdge saveWorkflowEdge(WorkflowEdge workflowEdge){
        return workflowEdgeRepository.save(workflowEdge);
    }

    public Workflow saveWorkflow(Workflow workflow){
        return workflowRepository.save(workflow);
    }

    public WorkflowEdge itemWorkflowEdge(Integer id) throws Exception {
        return workflowEdgeRepository.findByWorkflowId(id).orElseThrow(() -> new Exception("未查找到该工作流信息"));
    }

    public Workflow itemWorkflow(Integer id) throws Exception {
        return workflowRepository.findByWorkflowId(id).orElseThrow(() -> new Exception("未查找到该工作流基本信息"));
    }


}
