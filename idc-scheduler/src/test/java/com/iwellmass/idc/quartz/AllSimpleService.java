package com.iwellmass.idc.quartz;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedAcyclicGraph;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.WorkflowEdge;

public class AllSimpleService implements IDCPluginService {
	
	private final Map<String, Graph<TaskKey, WorkflowEdge>> workflowMap = new HashMap<>(); 
	
	private final Map<TaskKey, Task> taskMap = new HashMap<>();

	public Task getTask(TaskKey taskKey) {
		return taskMap.get(taskKey);
	}
	
	@Override
	public List<TaskKey> getSuccessors(TaskKey parentTaskKey, TaskKey taskKey) {
		Graph<TaskKey, WorkflowEdge> wf = workflowMap.get(parentTaskKey);
		return Graphs.successorListOf(wf, taskKey).stream().filter(t -> {
			return !t.equals(WorkflowEdge.START) && !t.equals(WorkflowEdge.END);
		}).collect(Collectors.toList());
	}

	@Override
	public List<TaskKey> getPredecessors(TaskKey parentTaskKey, TaskKey taskKey) {
		Graph<TaskKey, WorkflowEdge> wf = workflowMap.get(parentTaskKey);
		return Graphs.predecessorListOf(wf, taskKey).stream().filter(t -> {
			return !t.equals(WorkflowEdge.START) && !t.equals(WorkflowEdge.END);
		}).collect(Collectors.toList());
	}

	private static final Map<JobKey, Job> jobMap = new HashMap<>();
	
	@Override
	public Job getJob(JobKey jobKey) {
		return jobMap.get(jobKey);
	}

    @Override
	public void saveJob(Job job) {
		jobMap.put(job.getJobKey(), job);
	}
	
	public void addTaskDependency(String workflowId, TaskKey... depChain) {
		Graph<TaskKey, WorkflowEdge> graph = workflowMap.get(workflowId);
		if (graph == null) {
			graph = new DirectedAcyclicGraph<>(WorkflowEdge.class);
			graph.addVertex(WorkflowEdge.START);
			graph.addVertex(WorkflowEdge.END);
			workflowMap.put(workflowId, graph);
		}
		graph.addVertex(depChain[0]);
		for (int i = 0; i < depChain.length - 1; i++) {
			TaskKey src = depChain[i];
			TaskKey target = depChain[i + 1];
			graph.addVertex(target);
			graph.addEdge(src, target);
		}
	}

	@Override
	public List<JobDependency> getJobDependencies(JobKey jobKey) {
		return Collections.emptyList();
	}

	@Override
	public IDCPluginConfig getConfig() {
		return null;
	}

	@Override
	public List<WorkflowEdge> getTaskDependencies(TaskKey taskKey) {
		// TODO Auto-generated method stub
		return null;
	}

}
