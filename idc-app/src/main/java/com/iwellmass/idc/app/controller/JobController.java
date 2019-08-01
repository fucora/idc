package com.iwellmass.idc.app.controller;

import static com.iwellmass.idc.scheduler.util.IDCConstants.MSG_OP_SUCCESS;

import java.util.List;

import javax.annotation.Resource;

import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.app.vo.*;
import com.iwellmass.idc.message.FinishMessage;
import com.iwellmass.idc.scheduler.model.ExecutionLog;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.*;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.service.JobService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job")
public class JobController {
	
	@Resource
	JobService jobService;
	
    @ApiOperation("获取 JOB 运行状态")
    @PostMapping("/runtime")
    public ServiceResult<PageData<JobRuntimeVO>> runtime(@RequestBody(required = false) JobQueryParam qm) {
        PageData<JobRuntimeVO> taskInstance = jobService.query(qm);
        return ServiceResult.success(taskInstance);
    }
    
    @ApiOperation("获取 Job 详情")
    @GetMapping("/{id}")
    public ServiceResult<JobVO> get(@PathVariable("id") String id) {
    	JobVO jobVO = new JobVO();
    	jobService.get(id);
    	return ServiceResult.success(jobVO);
    }
    
    @ApiOperation("获取所有责任人")
    @GetMapping("/assignee")
    public ServiceResult<List<Assignee>> assignee() {
        return ServiceResult.success(jobService.getAllAssignee());
    }

    @ApiOperation("重跑任务")
    @PostMapping("/{id}/redo")
    public ServiceResult<String> redo(@PathVariable(name = "id") String id) {
        jobService.redo(id);
        return ServiceResult.success("success");
    }

    @ApiOperation("取消任务")
    @PostMapping("/{id}/cancel")
    public ServiceResult<String> cancel(@PathVariable(name = "id") Integer id) {
        return ServiceResult.success("success");
    }

    @ApiOperation("强制结束任务")
    @PostMapping("/{id}/force-complete")
    public ServiceResult<String> forceComplete(@PathVariable(name = "id") String id) {
        jobService.forceFinish(id);
        return ServiceResult.success("success");
    }

//    @ApiOperation("任务日志(分页)")
//    @PostMapping("/{id}/log")
//    public ServiceResult<PageData<ExecutionLog>> getLog(@PathVariable(name = "id") Integer id, Pager pager) {
//        PageData<ExecutionLog> data = jobInstanceService.getJobInstanceLog(id, pager);
//        return ServiceResult.success(data);
//    }
    
    @ApiOperation("测试执行")
    @GetMapping("/{id}/test/{action}")
    public ServiceResult<String> getWorkflowTask(@PathVariable("id") String id, @PathVariable("action") String action) {
        jobService.test(id, action);
        return ServiceResult.success(MSG_OP_SUCCESS);
    }


    @ApiModelProperty("查询调度计划下的node实例")
    @GetMapping("/{instanceId}/detail")
    public ServiceResult<JobVO> getPlanInstanceDetail(@PathVariable(name = "instanceId") String instanceId) {
        return ServiceResult.success(jobService.getPlanInstanceDetail(instanceId));
    }

    @ApiOperation("任务日志(分页)")
    @PostMapping("/{id}/log")
    public ServiceResult<PageData<ExecutionLog>> getLog(@PathVariable(name = "id") String id, Pager pager) {
        PageData<ExecutionLog> data = jobService.getJobInstanceLog(id, pager);
        return ServiceResult.success(data);
    }

}
