package com.iwellmass.idc.app.service;

import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.app.repo.JobRepository;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class DependencyServiceImpl implements DependencyService {

    @Inject
    private TaskRepository taskRepository;

    @Inject
    private JobRepository jobRepository;

    @Override
    public List<TaskKey> getSuccessors(String workflowId, TaskKey taskKey) {
        List<TaskKey> taskKeys = new ArrayList<>();
        for (Task task : taskRepository.findSuccessors(workflowId, taskKey)) {
            taskKeys.add(new TaskKey(task.getTaskId(), task.getTaskGroup()));
        }
        return taskKeys;
    }

    @Override
    public List<TaskKey> getPredecessors(String workflowId, TaskKey taskKey) {
        List<TaskKey> taskKeys = new ArrayList<>();
        for (Task task : taskRepository.findPredecessors(workflowId, taskKey)) {
            taskKeys.add(new TaskKey(task.getTaskId(), task.getTaskGroup()));
        }
        return taskKeys;
    }
}
