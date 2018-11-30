package com.iwellmass.idc.app.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.app.service.WorkflowService;
import com.iwellmass.idc.model.Workflow;
import com.iwellmass.idc.model.WorkflowEdge;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Inject
    private WorkflowService workflowService;

    @PostMapping("/edge")
    @ApiOperation("保存工作流边点信息")
    public ServiceResult<WorkflowEdge> saveWorkflowEdge(@RequestBody WorkflowEdge workflowEdge){
        return ServiceResult.success(workflowService.saveWorkflowEdge(workflowEdge));
    }

    @PostMapping("")
    @ApiOperation("保存工作流基本信息")
    public ServiceResult<Workflow> saveWorkflow(@RequestBody Workflow workflow){
        return ServiceResult.success(workflowService.saveWorkflow(workflow));
    }

    @GetMapping("/edge/{id}")
    @ApiOperation("查询工作流边点信息")
    public ServiceResult<WorkflowEdge> itemWorkflowEdge(@PathVariable("id") Integer id){
        try {
            return ServiceResult.success(workflowService.itemWorkflowEdge(id));
        } catch (Exception e) {
            return ServiceResult.failure(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("查询工作流基本信息")
    public ServiceResult<Workflow> itemWorkflow(@PathVariable("id") Integer id){
        try {
            return ServiceResult.success(workflowService.itemWorkflow(id));
        } catch (Exception e) {
            return ServiceResult.failure(e.getMessage());
        }
    }

}
