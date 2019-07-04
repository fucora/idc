package com.iwellmass.idc.app.controller;

import static com.iwellmass.idc.scheduler.util.IDCConstants.MSG_OP_SUCCESS;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.service.JobService;
import com.iwellmass.idc.app.vo.Assignee;
import com.iwellmass.idc.app.vo.JobQueryParam;
import com.iwellmass.idc.app.vo.JobRuntimeVO;
import com.iwellmass.idc.app.vo.JobVO;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job")
public class JobController {
	
	@Resource
	JobService jobService;
	
    @ApiOperation("获取 JOB 运行状态")
    @PostMapping("/runtime")
    public ServiceResult<PageData<JobRuntimeVO>> runtime(
            @RequestParam(required = false) JobQueryParam qm, Pager pager) {
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
//
//    @ApiOperation("重跑任务")
//    @PostMapping("/{id}/redo")
//    public ServiceResult<String> restart(@PathVariable(name = "id") Integer id, @RequestBody(required = false) RedoRequest redoRequest) {
//
//        if (redoRequest == null) {
//            redoRequest = new RedoRequest();
//        }
//        redoRequest.setInstanceId(id);
//        jobInstanceService.redo(redoRequest);
//        return ServiceResult.success("success");
//    }
//
//    @ApiOperation("取消任务")
//    @PostMapping("/{id}/cancle")
//    public ServiceResult<String> cancle(@PathVariable(name = "id") Integer id, @RequestBody(required = false) CancleRequest redoRequest) {
//        redoRequest.setInstanceId(id);
//        jobInstanceService.cancle(redoRequest);
//        return ServiceResult.success("success");
//    }
//
//    @ApiOperation("强制结束任务")
//    @PostMapping("/{id}/force-complete")
//    public ServiceResult<String> forceComplete(@PathVariable(name = "id") Integer id) {
//        jobInstanceService.forceComplete(id);
//        return ServiceResult.success("success");
//    }
//
//    @ApiOperation("任务日志(分页)")
//    @PostMapping("/{id}/log")
//    public ServiceResult<PageData<ExecutionLog>> getLog(@PathVariable(name = "id") Integer id, Pager pager) {
//        PageData<ExecutionLog> data = jobInstanceService.getJobInstanceLog(id, pager);
//        return ServiceResult.success(data);
//    }
    
    @ApiOperation("获取子任务实例")
    @GetMapping("/{id}/test/{action}")
    public ServiceResult<String> getWorkflowTask(@PathVariable("id") String id, @PathVariable("action") String action) {
        jobService.test(id, action);
        return ServiceResult.success(MSG_OP_SUCCESS);
    }

}
