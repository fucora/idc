package com.iwellmass.idc.app.controller;

import java.util.List;

import javax.inject.Inject;

import com.iwellmass.idc.model.TaskKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.app.service.WorkflowService;
import com.iwellmass.idc.model.Workflow;
import com.iwellmass.idc.model.WorkflowEdge;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Inject
    private WorkflowService workflowService;

    @GetMapping
    @ApiOperation("查询指定工作流")
    public ServiceResult<Workflow> get(@RequestParam("id") String id){
    	Workflow wf = workflowService.getWorkflow(id);
        return ServiceResult.success(wf);
    }
    
    @GetMapping("/edges")
    @ApiOperation("查询指定工作流")
    public ServiceResult<List<WorkflowEdge>> getEdges(TaskKey taskKey){
    	List<WorkflowEdge> wf = workflowService.getWorkflowEdges(taskKey);
    	return ServiceResult.success(wf);
    }
}
