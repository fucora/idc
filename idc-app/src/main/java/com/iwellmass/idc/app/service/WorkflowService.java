package com.iwellmass.idc.app.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.app.repo.WorkflowEdgeRepository;
import com.iwellmass.idc.app.repo.WorkflowRepository;
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
    	
    	edges.forEach(we -> {
    		we.setWorkflowId(workflow.getWorkflowId());
    	});
    	
    	workflowEdgeRepository.deleteByWorkflowId(workflow.getWorkflowId());
    	workflowEdgeRepository.save(edges);
    	workflowRepository.save(workflow);
    	return workflow;
    }

	public Workflow getWorkflow(String workflowId) {
		return workflowRepository.findOne(workflowId);
	}
}
