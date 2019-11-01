package com.iwellmass.idc.app.controller;

import static com.iwellmass.idc.scheduler.util.IDCConstants.MSG_OP_SUCCESS;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;

import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.app.service.JobHelper;
import com.iwellmass.idc.app.vo.*;
import com.iwellmass.idc.message.RedoMessage;
import com.iwellmass.idc.message.SkipMessage;
import com.iwellmass.idc.scheduler.model.ExecutionLog;
import io.swagger.annotations.ApiModelProperty;
import org.quartz.Scheduler;
import org.springframework.web.bind.annotation.*;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.app.service.JobService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job")
public class JobController {

    static final String OPT_SUCCESS = "success";

	@Resource
	JobService jobService;
    @Resource
    Scheduler qs;
    @Inject
    JobHelper jobHelper;

    @ApiOperation("获取 JOB 实例")
    @PostMapping("/runtime")
    public ServiceResult<PageData<JobRuntimeVO>> runtime(@RequestBody(required = false) JobQueryParam qm) {
        PageData<JobRuntimeVO> taskInstance = jobService.query(qm);
        return ServiceResult.success(taskInstance);
    }

    @ApiOperation("获取 Job 详情")
    @GetMapping("/{id}")
    public ServiceResult<JobVO> get(@PathVariable("id") String id) {
    	return ServiceResult.success(jobService.get(id));
    }

    @ApiOperation("获取所有责任人")
    @GetMapping("/assignee")
    public ServiceResult<List<Assignee>> assignee() {
        return ServiceResult.success(jobService.getAllAssignee());
    }

    @ApiOperation("重跑Job，NodeJob任务")
    @GetMapping("/{id}/redo")
    public ServiceResult<String> redo(@PathVariable(name = "id") String jobId) {
        RedoMessage message = RedoMessage.newMessage(jobId);
        message.setMessage("重启任务:" + jobId);
        TaskEventPlugin.eventService(qs).send(message);
        return ServiceResult.success("success");
    }

    @ApiOperation("取消任务:暂时没有取消的场景")
    @GetMapping("/{id}/cancel")
    public ServiceResult<String> cancel(@PathVariable(name = "id") Integer id) {
        return ServiceResult.success("success");
    }

    @ApiOperation("跳过指定任务")
    @GetMapping("/{id}/skip")
    public ServiceResult<String> skip(@PathVariable(name = "id") String jobId) {
        SkipMessage message = SkipMessage.newMessage(jobId);
        message.setMessage("跳过任务 jobId:" + jobId);
        TaskEventPlugin.eventService(qs).send(message);
        return ServiceResult.success("success");
    }

//    @ApiOperation("任务日志(分页)")
//    @PostMapping("/{id}/log")
//    public ServiceResult<PageData<ExecutionLog>> getLog(@PathVariable(name = "id") Integer id, Pager pager) {
//        PageData<ExecutionLog> data = jobInstanceService.getLogs(id, pager);
//        return ServiceResult.success(data);
//    }

    @ApiOperation("测试执行")
    @GetMapping("/{id}/test/{action}")
    public ServiceResult<String> getWorkflowTask(@PathVariable("id") String id, @PathVariable("action") String action) {
//        jobService.test(id, action);
        return ServiceResult.success(MSG_OP_SUCCESS);
    }


    @ApiModelProperty("查询调度计划下的node实例")
    @GetMapping("/{jobId}/detail")
    public ServiceResult<JobVO> getJobDetail(@PathVariable(name = "jobId") String jobId) {
        return ServiceResult.success(jobService.getJobDetail(jobId));
    }

    @ApiOperation("任务日志(分页)")
    @GetMapping("/log/{jobId}")
    public ServiceResult<PageData<ExecutionLog>> getLog(@PathVariable("jobId") String jobId) {
        PageData<ExecutionLog> data = jobService.getLogs(jobId);
        return ServiceResult.success(data);
    }

    @ApiOperation("test")
    @GetMapping("/test")
    public ServiceResult test() {
        return ServiceResult.success(jobService.test());
    }

    @ApiOperation("修改并发数")
    @PutMapping("/{maxRunningJobs}/modifyConcurrent")
    public ServiceResult<String> modifyConcurrent(@PathVariable(name = "maxRunningJobs") Integer maxRunningJobs) {
        jobHelper.modifyConcurrent(maxRunningJobs);
        return ServiceResult.success("success");
    }

    @ApiOperation("修改失败重试次数")
    @PutMapping(value = "/{retryCount}/modifyRetryCount")
    public ServiceResult<String> modifyRetryCount(@PathVariable(name = "retryCount") Integer retryCount) {
        jobHelper.modifyRetryCount(retryCount);
        return ServiceResult.success("success");
    }

    @ApiOperation("强制完成指定实例任务")
    @PutMapping("/{nodeJobId}/forceComplete")
    public ServiceResult<String> forceComplete(@PathVariable(name = "nodeJobId") String nodeJobId) {
        jobHelper.forceComplete(nodeJobId);
        return ServiceResult.success("success");
    }

    @ApiOperation("暂停job实例，同时暂停调度计划")
    @PutMapping("/{jobId}/pause")
    public ServiceResult<String> pause(@PathVariable(name = "jobId") String jobId) {
        jobHelper.pause(jobId);
        return ServiceResult.success("success");
    }


    @ApiOperation("恢复job实例，同时恢复调度计划")
    @PutMapping(value = "/{jobId}/resume")
    public ServiceResult<String> resume(@PathVariable(name = "jobId") String jobId) {
        jobHelper.resume(jobId);
        return ServiceResult.success("success");
    }

    @ApiOperation("推进该调度计划图")
    @PutMapping("/{addBatch}/{taskDependencyId}/executeTaskDependency")
    public ServiceResult<String> executeTaskDependency(@PathVariable(name = "addBatch") Integer addBatch,
                                                       @PathVariable(name = "taskDependencyId") Long taskDependencyId) {
        jobHelper.advanceTaskDependency(addBatch,taskDependencyId);
        return ServiceResult.success(OPT_SUCCESS);
    }

    @ApiOperation("推进某一个Job实例")
    @PutMapping("/{jobId}/{addBatch}/advanceJob")
    public ServiceResult<String> advanceJob(@PathVariable(name = "addBatch") Integer addBatch,
                                            @PathVariable(name = "jobId") String jobId) {
        jobHelper.advanceJob(addBatch,jobId);
        return ServiceResult.success(OPT_SUCCESS);
    }


}
