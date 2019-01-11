package com.iwellmass.idc.quartz;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.IDCPluginService;
import com.iwellmass.idc.model.*;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedAcyclicGraph;

public class AllSimpleService implements DependencyService, IDCPluginService {
	
	private final Map<String, Graph<TaskKey, WorkflowEdge>> workflowMap = new HashMap<>(); 
	
	private final Map<TaskKey, Task> taskMap = new HashMap<>();

	public Task findTask(TaskKey taskKey) {
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
	public Job findJob(JobKey jobKey) {
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
	public List<Task> findTasks(List<TaskKey> taskKey) {
		return taskKey.stream().map(taskMap::get).collect(Collectors.toList());
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
    public JobInstance findByInstanceId(Integer instanceId) {
        return null;
    }

	@Override
	public void saveJobDependencies(List<JobDependency> jobDependencies) {
		// 保存Job依赖关系
	}

	@Override
	public void clearJobDependencies(JobKey jobKey) {
		// 清除指定job的依赖关系
	}

}
