package com.iwellmass.idc.app.service;

import java.util.List;

import javax.inject.Inject;

import com.iwellmass.idc.model.TaskKey;
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
    		we.setParentTaskKey(new TaskKey(workflow.getTaskId(),workflow.getTaskGroup()));
    	});
    	
    	workflowEdgeRepository.deleteByParentTaskIdAndParentTaskGroup(workflow.getTaskId(),workflow.getTaskGroup());
    	workflowEdgeRepository.save(edges);
    	workflowRepository.save(workflow);
    	return workflow;
    }

	public Workflow getWorkflow(String workflowId) {
		return workflowRepository.findOne(workflowId);
	}

	public List<WorkflowEdge> getWorkflowEdges(TaskKey tk) {
		return workflowEdgeRepository.findByParentTaskIdAndParentTaskGroup(tk.getTaskId(),tk.getTaskGroup());
	}
}
