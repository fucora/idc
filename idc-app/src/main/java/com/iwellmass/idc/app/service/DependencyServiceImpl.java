package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.app.repo.WorkflowEdgeRepository;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.WorkflowEdge;

@Service
public class DependencyServiceImpl implements DependencyService {

    @Inject
    private WorkflowEdgeRepository workflowRepo;

	@Override
	public List<TaskKey> getSuccessors(String workflowId, TaskKey taskKey) {
		return workflowRepo.findSuccessors(workflowId, taskKey.getTaskId(), taskKey.getTaskGroup()).stream()
			.filter(edge -> !edge.getTaskKey().equals(WorkflowEdge.END))
			.map(WorkflowEdge::getTaskKey)
			.collect(Collectors.toList());
	}

	@Override
	public List<TaskKey> getPredecessors(String workflowId, TaskKey taskKey) {
		return workflowRepo.findPredecessors(workflowId, taskKey.getTaskId(), taskKey.getTaskGroup()).stream()
			.filter(edge -> !edge.getTaskKey().equals(WorkflowEdge.END))
			.map(WorkflowEdge::getSrcTaskKey)
			.collect(Collectors.toList());
	}
}
