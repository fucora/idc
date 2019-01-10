package com.iwellmass.idc.app.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.SimpleTaskVO;
import com.iwellmass.idc.app.service.TaskService;
import com.iwellmass.idc.app.vo.TaskQueryVO;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Inject
    private TaskService taskService;

    @ApiOperation("通过taskKey查询指定task")
    @PostMapping("/item")
    public ServiceResult<Task> get(@RequestBody TaskKey taskKey) {
        return ServiceResult.success(taskService.getTask(taskKey));
    }

    @ApiOperation("查询任务")
    @GetMapping
    public ServiceResult<Task> getTask(TaskKey taskKey) {
        Task task = taskService.getTask(taskKey);
        return ServiceResult.success(task);
    }

    @ApiOperation("新增任务")
    @PostMapping
    public ServiceResult<TaskKey> add(@RequestBody Task task) {
        taskService.add(task);
        return ServiceResult.success(task.getTaskKey());
    }

    @ApiOperation("更新任务")
    @PutMapping
    public ServiceResult<TaskKey> update(@RequestBody Task task) {
        taskService.update(task);
        return ServiceResult.success(task.getTaskKey());
    }

    @ApiOperation("查询任务列表")
    @PostMapping("/query")
    public ServiceResult<PageData<Task>> get(@RequestBody(required = false) TaskQueryVO taskQuery, Pager pager) {
        PageData<Task> ret = taskService.queryTask(taskQuery, pager);
        return ServiceResult.success(ret);
    }

    @ApiOperation("获取所有可用子任务")
    @GetMapping("/all-sub-task")
    public ServiceResult<List<Task>> getAllSubTask() {
        List<Task> ret = taskService.getTasksByType(TaskType.NODE_TASK);
        return ServiceResult.success(ret);
    }

    @ApiOperation("更改任务的工作流")
    @PostMapping("/graph")
    public ServiceResult<Task> modifyGraph(@RequestBody Task task) {
        try {
            return ServiceResult.success(taskService.modifyGraph(task));
        } catch (AppException e) {
            return ServiceResult.failure(e.getMessage());
        }
    }

    @ApiOperation("查询指定任务的参数")
    @GetMapping("/params")
    public ServiceResult<List<SimpleTaskVO>> getParams(TaskKey taskKey) {
        try {
            return ServiceResult.success(taskService.getParams(taskKey));
        } catch (AppException e) {
            return ServiceResult.failure(e.getMessage());
        }
    }


}
