package com.iwellmass.idc.app.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.app.service.WorkflowService;
import com.iwellmass.idc.model.Workflow;
import com.iwellmass.idc.model.WorkflowEdge;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Inject
    private WorkflowService workflowService;

    @PostMapping("/save/workflowedge")
    @ApiOperation("保存工作流任务")
    public ServiceResult<PageData<WorkflowEdge>> saveWorkflowEdge(@RequestBody WorkflowEdge workflowEdge){
        return ServiceResult.success(workflowService.saveWorkflowEdge(workflowEdge));
    }

    @PostMapping("/save/workflow")
    @ApiOperation("/保存工作流基本信息")
    public ServiceResult<PageData<Workflow>> saveWorkflow(@RequestBody Workflow workflow){
        return ServiceResult.success(workflowService.saveWorkflow(workflow));
    }

    

}
