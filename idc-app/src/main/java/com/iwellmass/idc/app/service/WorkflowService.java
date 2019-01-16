package com.iwellmass.idc.app.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
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
    public void saveWorkflow(Workflow workflow) {
    	
    	// validate
    	List<WorkflowEdge> edges = IDCUtils.parseWorkflowEdge(workflow.getGraph());
    	
    	// autoId
    	String autoId = Hashing.md5().hashString(workflow.getGraph(), Charsets.UTF_8).toString();
    	workflow.setWorkflowId(autoId);
    	
    	Workflow check = workflowRepository.findOne(autoId);
    	
    	if (check == null) {
    		// 没有找到这个工作流
    		workflow.setWorkflowId(autoId);
    		edges.forEach(we -> {
    			we.setParentTaskKey(new TaskKey(workflow.getTaskId(),workflow.getTaskGroup()));
    		});
    		workflowRepository.save(workflow);
    		// 刷新 edges
    		workflowEdgeRepository.deleteByParentTaskIdAndParentTaskGroup(workflow.getTaskId(),workflow.getTaskGroup());
    		workflowEdgeRepository.save(edges);
    	}
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
