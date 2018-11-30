package com.iwellmass.idc.app.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.TaskService;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/task")
public class TaskController {
	
	@Inject
    private TaskService taskService;
	
    @ApiOperation("保存任务")
    @PostMapping
    public ServiceResult<TaskKey> add(@RequestBody Task task){
    	taskService.saveTask(task);
        return ServiceResult.success(task.getTaskKey());
    }
    
    @ApiOperation("获取所有工作流子任务")
    @GetMapping("/all-sub-task")
    public ServiceResult<List<Task>> getAllSubTask() {
    	List<Task> ret = taskService.getTasksByType(TaskType.WORKFLOW_SUB_TASK);
    	return ServiceResult.success(ret);
    }
}
