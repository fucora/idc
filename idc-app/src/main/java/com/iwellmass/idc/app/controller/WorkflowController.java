package com.iwellmass.idc.app.controller;

import javax.inject.Inject;

import com.iwellmass.idc.app.vo.WorkflowSaveVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.app.service.WorkflowService;
import com.iwellmass.idc.app.vo.WorkflowEnableVO;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.Workflow;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Inject
    private WorkflowService workflowService;

    @PostMapping("")
    @ApiOperation("保存workflow画图信息,以及最新workflowId")
    public ServiceResult<Workflow> save(@RequestBody WorkflowSaveVO workflowSaveVO) {
        try {
            return ServiceResult.success(workflowService.saveWorkflow(workflowSaveVO));
        } catch (Exception e) {
            return ServiceResult.failure(e.getMessage());
        }
    }

    @GetMapping("")
    @ApiOperation("查询指定工作流")
    public ServiceResult<Workflow> item(TaskKey taskKey){
        try {
            return ServiceResult.success(workflowService.item(taskKey));
        } catch (Exception e) {
            return ServiceResult.failure(e.getMessage());
        }
    }

    @PostMapping("enable")
    @ApiOperation("/启用指定工作流")
    public ServiceResult<String> enable(@RequestBody WorkflowEnableVO workflowEnableVO) {
        try {
            return ServiceResult.success(workflowService.enable(workflowEnableVO));
        } catch (Exception e) {
            return ServiceResult.failure(e.getMessage());
        }
    }



}
