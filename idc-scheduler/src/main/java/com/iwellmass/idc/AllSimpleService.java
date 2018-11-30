package com.iwellmass.idc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskEdge;
import com.iwellmass.idc.model.TaskKey;

public class AllSimpleService implements WorkflowService, TaskService, JobService {
	
	private final Map<Integer, Graph<TaskKey, TaskEdge>> workflowMap = new HashMap<>(); 
	
	private final Map<TaskKey, Task> taskMap = new HashMap<>();

	public Task getTask(TaskKey taskKey) {
		return taskMap.get(taskKey);
	}
	
//	public void addTask(Task mainTask, Task subTask, TaskKey prev, TaskKey next) {
//		
//		Graph<TaskKey, TaskEdge> graph = workflowMap.get(mainTask.getWorkflowId());
//		if (graph == null) {
//			graph = new DirectedAcyclicGraph<>(TaskEdge.class);
//			graph.addVertex(START);
//			graph.addVertex(END);
//			workflowMap.put(mainTask.getWorkflowId(), graph);
//		}
//		
//		taskMap.put(mainTask.getTaskKey(), mainTask);
//		taskMap.put(subTask.getTaskKey(), subTask);
//		
//		graph.addVertex(prev);
//		graph.addVertex(subTask.getTaskKey());
//		graph.addVertex(next);
//		
//		graph.addEdge(prev, subTask.getTaskKey());
//		graph.addEdge(subTask.getTaskKey(), next);
//	}

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
	
}
