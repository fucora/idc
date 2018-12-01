package com.iwellmass.idc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedAcyclicGraph;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskEdge;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.model.WorkflowEdge;

public class AllSimpleService implements DependencyService, TaskService, JobService {
	
	private final Map<String, Graph<TaskKey, TaskEdge>> workflowMap = new HashMap<>(); 
	
	private final Map<TaskKey, Task> taskMap = new HashMap<>();

	public Task getTask(TaskKey taskKey) {
		return taskMap.get(taskKey);
	}
	
	@Override
	public List<TaskKey> getSuccessors(String workflowId, TaskKey taskKey) {
		Graph<TaskKey, TaskEdge> wf = workflowMap.get(workflowId);
		return Graphs.successorListOf(wf, taskKey).stream().filter(t -> {
			return !t.equals(WorkflowEdge.START) && !t.equals(WorkflowEdge.END);
		}).collect(Collectors.toList());
	}

	@Override
	public List<TaskKey> getPredecessors(String workflowId, TaskKey taskKey) {
		Graph<TaskKey, TaskEdge> wf = workflowMap.get(workflowId);
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
	
	@Override
	public void saveTask(Task task) {
		taskMap.put(task.getTaskKey(), task);
	}
	
	@Override
	public List<Task> getTasks(List<TaskKey> taskKey) {
		return taskKey.stream().map(taskMap::get).collect(Collectors.toList());
	}

	@Override
	public List<Task> getTasksByType(TaskType workflowSubTask) {
		return null;
	}

	public void addTaskDependency(String workflowId, TaskKey... depChain) {
		Graph<TaskKey, TaskEdge> graph = workflowMap.get(workflowId);
		if (graph == null) {
			graph = new DirectedAcyclicGraph<>(TaskEdge.class);
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

	
}
