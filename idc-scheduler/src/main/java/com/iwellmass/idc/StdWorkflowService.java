package com.iwellmass.idc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedAcyclicGraph;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskEdge;
import com.iwellmass.idc.model.TaskKey;

public class StdWorkflowService implements WorkflowService {
	
	private final Map<Integer, Graph<TaskKey, TaskEdge>> workflowMap = new HashMap<>(); 
	
	private final Map<TaskKey, Task> taskMap = new HashMap<>();

	public Task getTask(TaskKey taskKey) {
		return taskMap.get(taskKey);
	}
	
	public void addTask(Task mainTask, Task subTask, TaskKey prev, TaskKey next) {
		
		Graph<TaskKey, TaskEdge> graph = workflowMap.get(mainTask.getWorkflowId());
		if (graph == null) {
			graph = new DirectedAcyclicGraph<>(TaskEdge.class);
			graph.addVertex(START);
			graph.addVertex(END);
			workflowMap.put(mainTask.getWorkflowId(), graph);
		}
		
		taskMap.put(mainTask.getTaskKey(), mainTask);
		taskMap.put(subTask.getTaskKey(), subTask);
		
		graph.addVertex(prev);
		graph.addVertex(subTask.getTaskKey());
		graph.addVertex(next);
		
		graph.addEdge(prev, subTask.getTaskKey());
		graph.addEdge(subTask.getTaskKey(), next);
	}

	@Override
	public Graph<TaskKey, TaskEdge> getWorkflow(TaskKey taskKey) {
		Integer id = Objects.requireNonNull(taskMap.get(taskKey)).getWorkflowId();
		Graph<TaskKey, TaskEdge> aa = workflowMap.get(id);
		return Objects.requireNonNull(aa);
	}

	@Override
	public List<TaskKey> getSuccessors(int workflowId, TaskKey taskKey) {
		Graph<TaskKey, TaskEdge> wf = workflowMap.get(workflowId);
		return Graphs.successorListOf(wf, taskKey).stream().filter(t -> {
			return !t.equals(START) && !t.equals(END);
		}).collect(Collectors.toList());
	}

	@Override
	public List<TaskKey> getPredecessors(int workflowId, TaskKey taskKey) {
		Graph<TaskKey, TaskEdge> wf = workflowMap.get(workflowId);
		return Graphs.predecessorListOf(wf, taskKey).stream().filter(t -> {
			return !t.equals(START) && !t.equals(END);
		}).collect(Collectors.toList());
	}
}
