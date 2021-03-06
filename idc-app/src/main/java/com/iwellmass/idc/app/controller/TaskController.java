package com.iwellmass.idc.app.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.app.service.JobService;
import com.iwellmass.idc.app.service.TaskService;
import com.iwellmass.idc.app.vo.Assignee;
import com.iwellmass.idc.app.vo.TaskDependencyQueryVO;
import com.iwellmass.idc.app.vo.TaskQueryParam;
import com.iwellmass.idc.app.vo.TaskRuntimeVO;
import com.iwellmass.idc.app.vo.graph.TaskGraphVO;
import com.iwellmass.idc.app.vo.task.*;
import com.iwellmass.idc.model.CronType;
import com.iwellmass.idc.scheduler.model.IDCScheduler;
import com.iwellmass.idc.scheduler.model.Task;
import com.iwellmass.idc.scheduler.model.TaskDependency;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static com.iwellmass.idc.scheduler.util.IDCConstants.MSG_OP_SUCCESS;

@RestController
@RequestMapping("/task")
public class TaskController {

    static final String OPT_SUCCESS = "success";

    @Resource
    private IDCScheduler idcs;

    @Resource
    private TaskService taskService;

    @Autowired
    private JobService jobService;

    @ApiOperation("获取调度运行时信息")
    @PostMapping("/runtime")
    public ServiceResult<PageData<TaskRuntimeVO>> getTaskRuntime(@RequestBody TaskQueryParam jqm) {
        PageData<TaskRuntimeVO> ret = taskService.query(jqm);
        return ServiceResult.success(ret);
    }

    @ApiOperation("新增自动调度计划")
    @PostMapping
    public ServiceResult<String> schedule(@RequestBody TaskVO vo) {
        idcs.schedule(vo);
        return ServiceResult.success(MSG_OP_SUCCESS);
    }


    @ApiOperation("新增手动调度计划")
    @PostMapping("/manual")
    public ServiceResult<String> manualSchedule(@RequestBody TaskVO vo) {

        Task task = jobService.getTask(vo.getTaskName());
        Assert.notNull(task, String.format("taskName:[%s]不存在", vo.getTaskName()));

        idcs.scheduleJob(vo, task);
        return ServiceResult.success(MSG_OP_SUCCESS);
    }


    @ApiOperation("修改手动调度计划")
    @PutMapping("/manual")
    public ServiceResult<String> updateManualSchedule(@RequestBody ManualUpdateVo vo) {

        Task queryTask = jobService.getTask(vo.getTaskName());
        Assert.notNull(queryTask, String.format("taskName:[%s]不存在", vo.getTaskName()));

        setUpdateParams(vo, queryTask);
        idcs.updateScheduleTask(queryTask);

        return ServiceResult.success(MSG_OP_SUCCESS);
    }


    private void setUpdateParams(@RequestBody ManualUpdateVo vo, Task queryTask) {
        queryTask.setAssignee(vo.getAssignee());
        queryTask.setUpdatetime(LocalDateTime.now());
        queryTask.setBlockOnError(vo.getBlockOnError());
        queryTask.setIsRetry(vo.getIsRetry());
        queryTask.setDescription(vo.getDescription());
    }


    @ApiOperation("获取调度计划详情")
    @GetMapping("/{name}")
    public ServiceResult<TaskVO> getTask(@PathVariable("name") String name) {
        TaskVO vo = taskService.getTask(name);
        return ServiceResult.success(vo);
    }

    @ApiOperation("查询负责人信息")
    @GetMapping("/assignee")
    public ServiceResult<List<Assignee>> getAssignee() {
        List<Assignee> data = taskService.getAllAssignee();
        return ServiceResult.success(data);
    }

    @ApiOperation("重新调度")
    @PostMapping(path = "/reschedule")
    public ServiceResult<String> reschedule(@RequestBody ReTaskVO reTaskVO) {
        idcs.reschedule(reTaskVO);
        return ServiceResult.success(MSG_OP_SUCCESS);
    }

    @ApiOperation("取消调度")
    @GetMapping(path = "/{name}/unschedule")
    public ServiceResult<String> unschedule(@PathVariable("name") String name) {
        idcs.unschedule(name);
        return ServiceResult.success(MSG_OP_SUCCESS);
    }

    @GetMapping(value = "/{name}/pause")
    @ApiOperation("暂停调度")
    public ServiceResult<String> pause(@PathVariable("name") String name) {
        idcs.pause(name);
        return ServiceResult.success(MSG_OP_SUCCESS);
    }

    @GetMapping(value = "/{name}/resume")
    @ApiOperation("恢复调度")
    public ServiceResult<String> resume(@PathVariable("name") String name) {
        idcs.resume(name);
        return ServiceResult.success(MSG_OP_SUCCESS);
    }

    @ApiOperation("查询调度计划的运行参数")
    @GetMapping(path = "{workflowId}/params")
    public ServiceResult<List<MergeTaskParamVO>> getParams(@PathVariable(name = "workflowId") String workflowId) {
        return ServiceResult.success(taskService.getParams(workflowId));
    }

    @ApiOperation("loadDate参数简要可选字段")
    @GetMapping("/loadDateParams/simplify")
    public ServiceResult<List<String>> getLoadDateParams() {
        return ServiceResult.success(taskService.getLoadDateParams());
    }

    @ApiOperation("删除调度计划")
    @DeleteMapping("/{taskName}/delete")
    public ServiceResult<String> delete(@PathVariable(name = "taskName") String taskName) {
        taskService.delete(taskName);
        return ServiceResult.success(MSG_OP_SUCCESS);
    }

    @ApiOperation("根据任务名查询调度计划依赖查看")
    @GetMapping("/{taskName}/getTaskDependenciesByTaskName")
    public ServiceResult<TaskGraphVO> getTaskDependenciesByTaskName(@PathVariable(name = "taskName") String taskName) {
        return ServiceResult.success(taskService.getTaskDependenciesByTaskName(taskName));
    }

    @ApiOperation("查看调度计划依赖图及其运行信息")
    @GetMapping("/{taskDependencyId}/getTaskDependenciesByTaskName")
    public ServiceResult<TaskGraphVO> getTaskDependenciesById(@PathVariable(name = "taskDependencyId") Long taskDependencyId) {
        return ServiceResult.success(taskService.getTaskDependenciesById(taskDependencyId));
    }

    @ApiOperation("获取调度运行时信息")
    @GetMapping("{cronType}/getTaskToDependency")
    public ServiceResult<List<TaskRuntimeVO>> getTaskToDependency(@PathVariable(name = "cronType") CronType cronType) {
        return ServiceResult.success(taskService.getTaskToDependency(cronType));
    }

    @ApiOperation("创建调度计划依赖")
    @PostMapping("/dependency")
    public ServiceResult<String> saveDependency(@RequestBody TaskDependencyVO taskDependencyVO) {
        taskService.saveDependency(taskDependencyVO);
        return ServiceResult.success(OPT_SUCCESS);
    }

    @ApiOperation("保存或更新调度计划依赖边")
    @PostMapping("/{id}/dependency/edge")
    public ServiceResult<String> saveDependencyEdge(@PathVariable(name = "id") Long id, @RequestBody TaskGraphVO taskGraphVO) {
        taskService.saveDependencyEdge(id, taskGraphVO);
        return ServiceResult.success(OPT_SUCCESS);
    }

    @ApiOperation("查询调度计划依赖图")
    @PostMapping("/queryTaskDependencies")
    public ServiceResult<List<TaskDependency>> queryTaskDependencies(@RequestBody TaskDependencyQueryVO taskDependencyQueryVO) {
        return ServiceResult.success(taskService.queryTaskDependencies(taskDependencyQueryVO));
    }

    @ApiOperation("查询调度计划依赖图可以选择调度计划")
    @GetMapping("/{dependencyId}/queryDependencyTasks")
    public ServiceResult<List<Task>> queryDependencyTasks(@PathVariable(name = "dependencyId") Long taskDependencyId) {
        return ServiceResult.success(taskService.queryCanDrawTask(taskDependencyId));
    }
}
