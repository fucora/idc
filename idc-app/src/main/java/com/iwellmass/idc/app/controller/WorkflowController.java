package com.iwellmass.idc.app.controller;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.app.service.WorkflowService;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.Workflow;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Inject
    private WorkflowService workflowService;

    @GetMapping
    @ApiOperation("查询指定工作流")
    public ServiceResult<Workflow> item(TaskKey taskKey){
        try {
            return ServiceResult.success(workflowService.item(taskKey));
        } catch (Exception e) {
            return ServiceResult.failure(e.getMessage());
        }
    }

}
