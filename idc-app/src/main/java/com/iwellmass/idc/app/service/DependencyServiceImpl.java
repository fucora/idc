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
	public List<TaskKey> getSuccessors(TaskKey parentTaskKey, TaskKey taskKey) {
		return workflowRepo.findSuccessors(parentTaskKey.getTaskId(),parentTaskKey.getTaskGroup(), taskKey.getTaskId(), taskKey.getTaskGroup()).stream()
			.map(WorkflowEdge::getTaskKey)
			.collect(Collectors.toList());
	}

	@Override
	public List<TaskKey> getPredecessors(TaskKey parentTaskKey, TaskKey taskKey) {
		return workflowRepo.findPredecessors(parentTaskKey.getTaskId(),parentTaskKey.getTaskGroup(), taskKey.getTaskId(), taskKey.getTaskGroup()).stream()
			.map(WorkflowEdge::getSrcTaskKey)
			.collect(Collectors.toList());
	}

	@Override
	public List<JobDependency> getJobDependencies(JobKey jobKey) {
		return jobDependencyRepo.findDependencies(jobKey.getJobId(), jobKey.getJobGroup());
	}
}
