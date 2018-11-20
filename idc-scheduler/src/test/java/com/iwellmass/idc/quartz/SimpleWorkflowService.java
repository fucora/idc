package com.iwellmass.idc.quartz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedAcyclicGraph;

import com.iwellmass.idc.dag.WorkflowService;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;

public class SimpleWorkflowService extends WorkflowService {
	
	private final Map<Integer, Graph<TaskKey, Flow>> workflowMap = new HashMap<>(); 
	
	private final Map<TaskKey, Task> taskMap = new HashMap<>();

	@Override
	public List<Task> successors(Integer workflowId, TaskKey taskKey) {
		Graph<TaskKey, Flow> graph = workflowMap.get(workflowId);
		List<TaskKey> successorListOf = Graphs.successorListOf(graph, taskKey);
		return successorListOf.stream().map(this::getTask).collect(Collectors.toList());
	}
	
	public Task getTask(TaskKey taskKey) {
		return taskMap.get(taskKey);
	}
	
	public void addTask(Integer workflowId, Task node, TaskKey prev, TaskKey next) {
		Graph<TaskKey, Flow> graph = workflowMap.get(workflowId);
		if (graph == null) {
			graph = new DirectedAcyclicGraph<>(Flow.class);
			graph.addVertex(START);
			graph.addVertex(END);
			graph.addVertex(node.getTaskKey());
			workflowMap.put(workflowId, graph);
			taskMap.put(node.getTaskKey(), node);
		}
		graph.addEdge(prev, node.getTaskKey());
		graph.addEdge(node.getTaskKey(), next);
	}
	
	public static class Flow {}
	
	public static void main(String[] args) {
		System.out.println(new TaskKey("a", "b").equals(new TaskKey("a", "b")));
	}
}
