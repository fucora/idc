package com.iwellmass.idc.app.service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.app.repo.WorkflowEdgeRepository;
import com.iwellmass.idc.app.repo.WorkflowRepository;
import com.iwellmass.idc.model.Workflow;
import com.iwellmass.idc.model.WorkflowEdge;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;

@Service
public class WorkflowService {

    @Inject
    private WorkflowRepository workflowRepository;
    @Inject
    WorkflowEdgeRepository workflowEdgeRepository;

    public PageData<WorkflowEdge> saveWorkflowEdge(WorkflowEdge workflowEdge){
        return new PageData<>(1,Arrays.asList(workflowEdgeRepository.save(workflowEdge)));
    }

    public PageData<Workflow> saveWorkflow(Workflow workflow){
        return new PageData<>(1,Arrays.asList(workflowRepository.save(workflow)));
    }


}
