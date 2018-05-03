package com.iwellmass.ddc.server.dag;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.iwellmass.dispatcher.thrift.bvo.WorkflowTask;

/**
 * Description goes here.
 *
 * @author Lu Gan
 * @email lu.gan@dmall.com
 * @date 4/20/16
 */
public class TaskDAGBuilder {

    private final Map<Integer, TaskWithDependencies> dependencyMap;
    private final Map<Integer, WorkflowTask> templateMap;

    public TaskDAGBuilder(WorkflowTask template) {
        ImmutableMap.Builder<Integer, TaskWithDependencies> dependencyMapBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<Integer, WorkflowTask> jobsBuilder = ImmutableMap.builder();
        build(template, dependencyMapBuilder, jobsBuilder);

        dependencyMap = dependencyMapBuilder.build();
        templateMap = jobsBuilder.build();
    }

    public Map<Integer, TaskWithDependencies> getDependencyMap() {
        return dependencyMap;
    }

    public Map<Integer, WorkflowTask> getTemplateMap() {
        return templateMap;
    }

    private void build(WorkflowTask template, ImmutableMap.Builder<Integer, TaskWithDependencies> dependencyMapBuilder, ImmutableMap.Builder<Integer, WorkflowTask> jobsBuilder) {
        DefaultDirectedGraph<Integer, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        worker(graph, template, null, jobsBuilder, Sets.newHashSet(), 0);

        CycleDetector<Integer, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
        if (cycleDetector.detectCycles()) {
            throw new RuntimeException("The Job DAG contains cycles: " + template);
        }

        TopologicalOrderIterator<Integer, DefaultEdge> orderIterator = new TopologicalOrderIterator(graph);
        while (orderIterator.hasNext()) {
            Integer taskId = orderIterator.next();
            Set<DefaultEdge> jobIdEdges = graph.edgesOf(taskId);
            Set<Integer> processed = jobIdEdges.stream().map(graph::getEdgeSource).filter(edge -> edge != null && !edge.equals(taskId)).collect(Collectors.toSet());
            dependencyMapBuilder.put(taskId, new TaskWithDependencies(taskId, processed));
        }
    }

    private void worker(DefaultDirectedGraph<Integer, DefaultEdge> graph, WorkflowTask task, Integer parentId,
                        ImmutableMap.Builder<Integer, WorkflowTask> tasksBuilder, Set<Integer> usedTasksSet, int depth) {
        if (usedTasksSet.add(task.getTaskId())) {
            tasksBuilder.put(task.getTaskId(), task);
        }

        graph.addVertex(task.getTaskId());
        if ( parentId != null )
        {
            graph.addEdge(parentId, task.getTaskId());
        }
        if(task.getChildren() != null) {
            task.getChildren().forEach(child -> worker(graph, child, task.getTaskId(), tasksBuilder, usedTasksSet, depth + 1));
        }
    }
}
