package com.iwellmass.idc.app.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.scheduler.model.Workflow;
import com.iwellmass.idc.scheduler.repository.WorkflowRepository;

@Service
public class WorkflowService {

    @Inject
    private WorkflowRepository workflowRepository;



    @Transactional
    public void saveWorkflow(Workflow workflow) {
    	
    	// validate
//    	// List<WorkflowEdge> edges = IDCUtils.parseWorkflowEdge(workflow.getGraph());
//    	
//    	// autoId
//    	String autoId = Hashing.md5().hashString(workflow.getGraph(), Charsets.UTF_8).toString();
//    	workflow.setWorkflowId(autoId);
//    	
//    	Workflow check = workflowRepository.findById(autoId).get();
//    	
//    	if (check == null) {
//    		// 没有找到这个工作流
//    		workflow.setWorkflowId(autoId);
//    		edges.forEach(we -> {
//    			// we.setParentTaskKey(new TaskKey(workflow.getTaskId(),workflow.getTaskGroup()));
//    		});
//    		workflowRepository.save(workflow);
//    		// 刷新 edges
//    		// workflowEdgeRepository.deleteByParentTaskIdAndParentTaskGroup(workflow.getTaskId(),workflow.getTaskGroup());
//    		workflowEdgeRepository.saveAll(edges);
//    	}
    }

	public Workflow getWorkflow(String workflowId) {
		return workflowRepository.findById(workflowId).get();
	}


	public Workflow findOne(String workflowId) {
		return workflowRepository.findById(workflowId).get();
	}
}
