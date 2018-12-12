package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.app.repo.JobDependencyRepository;
import com.iwellmass.idc.app.repo.WorkflowEdgeRepository;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.WorkflowEdge;

@Service
public class DependencyServiceImpl implements DependencyService {

    @Inject
    private WorkflowEdgeRepository workflowRepo;
    
    @Inject
    private JobDependencyRepository jobDependencyRepo;

	@Override
	public List<TaskKey> getSuccessors(String workflowId, TaskKey taskKey) {
		return workflowRepo.findSuccessors(workflowId, taskKey.getTaskId(), taskKey.getTaskGroup()).stream()
			.map(WorkflowEdge::getTaskKey)
			.collect(Collectors.toList());
	}

	@Override
	public List<TaskKey> getPredecessors(String workflowId, TaskKey taskKey) {
		return workflowRepo.findPredecessors(workflowId, taskKey.getTaskId(), taskKey.getTaskGroup()).stream()
			.map(WorkflowEdge::getSrcTaskKey)
			.collect(Collectors.toList());
	}

	@Override
	public List<JobDependency> getJobDependencies(JobKey jobKey) {
		return jobDependencyRepo.findDependencies(jobKey.getJobId(), jobKey.getJobGroup());
	}
}
