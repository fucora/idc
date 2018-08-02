package com.iwellmass.ddc.server.dag;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.dag.SchedulingEngine;
import com.iwellmass.dispatcher.common.entry.TaskInfoTuple;
import com.iwellmass.dispatcher.thrift.bvo.WorkflowTask;

/**
 *
 * Not thread safe.
 *
 * @author Lu Gan
 * @email lu.gan@dmall.com
 * @date 4/20/16
 */
@Service
public class DAGSchedulingEngine implements SchedulingEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(DAGSchedulingEngine.class);

    private static final String SCHEDULING_ENGINE_NAME = "DAG Based Scheduling Engine";

//    private Map<Integer, TaskDAGBuilder> workflowTemplates = new HashMap<>();
    @Autowired
    private WorkflowManager workflowManager;

    @Autowired
    private TaskStatusManager jobStatusManager;

    private String instanceId;

    private volatile boolean closed = false;
    private volatile boolean shuttingDown = false;


    @Override
    public String getInstanceId() {
        return String.format("%s [%s]", SCHEDULING_ENGINE_NAME, UUID.randomUUID());
    }

    @Override
    public void start() {
        if (shuttingDown|| closed) {
            throw new RuntimeException("The SchedulingEngine cannot be restarted after shutdown() has been called.");
        }

        instanceId = String.format("%s [%s]", SCHEDULING_ENGINE_NAME, UUID.randomUUID());

        initWorkflows();

        LOGGER.info(instanceId + "started.");
    }

    private void initWorkflows() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void shutdownGracefully() {

    }

    @Override
    public boolean isShutdown() {
        return closed;
    }

    @Override
    public List<Integer> findSubsequentTasks(TaskInfoTuple tuple) {
        final Long workflowExecuteId = tuple.getWorkflowExecuteId();
        Objects.requireNonNull(workflowExecuteId, "Workflow Execute Id must not be null!");
        final Integer taskId = tuple.getTaskId();
        Objects.requireNonNull(taskId, "Task Id must not be null!");
        final String executeBatchId = tuple.getExecuteBatchId();
        Objects.requireNonNull(executeBatchId, "Execute Batch Id must not be null!");
        final Integer workflowId = tuple.getWorkflowId();
        Objects.requireNonNull(workflowId, "Workflow Id must not be null!");

        final TaskDAGBuilder taskDAGBuilder = workflowManager.get(workflowId).orElseThrow(() -> new IllegalArgumentException(String.format("流程编号{%d}对应的流程信息不存在！", workflowId)));

        final WorkflowTask task = Optional.ofNullable(taskDAGBuilder.getTemplateMap()).
                flatMap(dependencyMap -> Optional.ofNullable(dependencyMap.get(taskId))).
                orElseThrow(() -> new IllegalArgumentException(String.format("任务编号{%d}对应的任务不存在！", taskId)));

        final List<WorkflowTask> childTasks = task.getChildren();

        Set<Integer> result = childTasks.stream().flatMap(child -> {
            final TaskWithDependencies childTaskWithDeps = Optional.ofNullable(taskDAGBuilder.getDependencyMap()).
                    flatMap(dependencyMap -> Optional.ofNullable(dependencyMap.get(child.getTaskId()))).
                    orElseThrow(() -> new IllegalArgumentException("TaskWithDependencies not found!"));
            Collection<Integer> currTaskDeps = childTaskWithDeps.getDependencies();
            if(jobStatusManager.isJobsCompleted(workflowExecuteId, executeBatchId, currTaskDeps)) {
            	return Stream.of(child.getTaskId());
            } else {
                return Stream.empty();
            }
        }).collect(Collectors.toSet());

        return result.stream().collect(Collectors.toList());
    }

    @Override
    public List<Integer> findSubsequentTasks(List<TaskInfoTuple> tuples) {
        return tuples.stream().flatMap(taskStatusInfo -> findSubsequentTasks(taskStatusInfo).stream()).collect(Collectors.toList());
    }

    @Override
    public List<Integer> findStartTaskIds(Integer workflowId) {

    	final TaskDAGBuilder taskDAGBuilder = workflowManager.get(workflowId).orElseThrow(() -> new IllegalArgumentException(String.format("流程编号{%d}对应的流程信息不存在！", workflowId)));

        WorkflowTask tt = taskDAGBuilder.getTemplateMap().get(Constants.WORKFLOW_START_TASK_ID);
        return tt.getChildren().stream().map(t -> t.getTaskId()).collect(Collectors.toList());

    }

}
