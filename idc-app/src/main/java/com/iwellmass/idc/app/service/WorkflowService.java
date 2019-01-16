package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.iwellmass.idc.model.TaskKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
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
    public boolean saveWorkflow(Workflow workflow) {
    	
    	List<WorkflowEdge> edges = IDCUtils.parseWorkflowEdge(workflow.getGraph());
    	
    	List<String> ks = edges.stream().sorted().map(e -> {
    		return e.getTaskKey().toString() + e.getSrcTaskKey().toString();
    	}).collect(Collectors.toList());
    	
    	String autoId = Hashing.md5().hashString(String.join(",", ks), Charsets.UTF_8).toString();

    	Workflow check = workflowRepository.findOne(autoId);
    	if (check != null) {
    		workflow.setWorkflowId(autoId);
    		edges.forEach(we -> {
    			we.setParentTaskKey(new TaskKey(workflow.getTaskId(),workflow.getTaskGroup()));
    		});
    		workflowRepository.save(workflow);
    		// 刷新 edges
    		workflowEdgeRepository.deleteByParentTaskIdAndParentTaskGroup(workflow.getTaskId(),workflow.getTaskGroup());
    		workflowEdgeRepository.save(edges);
    		return true;
    	}
    	return false;
    }

	public Workflow getWorkflow(String workflowId) {
		return workflowRepository.findOne(workflowId);
	}

	public List<WorkflowEdge> getWorkflowEdges(TaskKey tk) {
		return workflowEdgeRepository.findByParentTaskIdAndParentTaskGroup(tk.getTaskId(),tk.getTaskGroup());
	}

	public Workflow findOne(String workflowId) {
		return workflowRepository.findOne(workflowId);
	}
}
